package com.dodax.jobs;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Job for parallel execution of the supplied IJob list and specified thread count.
 * Runtime exception in any provided job leads to interruption of all the other jobs.
 */
public class ExecutorJob implements IJob {
  /**
   * Jobs which would be executed in parallel
   */
  private final List<IJob> jobsForExec;
  /**
   * Internal executor service for parallelization
   */
  private final ExecutorService execService;

  /**
   * Constructs ExecutorJob for {@code jobsForExec} parallel execution with {@code threadCount} threads
   *
   * @param jobsForExec list of IJobs for parallel execution
   * @param threadCount number of parallel threads
   */
  public ExecutorJob(final List<IJob> jobsForExec, final int threadCount) {
    this.jobsForExec = jobsForExec;
    this.execService = Executors.newFixedThreadPool(threadCount);
  }

  @Override
  public void execute() {
    // Futures for jobsForExec tasks
    final List<Future> futureList = new ArrayList<>();
    try {
      // Construct CompletionService for online checking completed tasks queue
      final CompletionService execCompletionService = new ExecutorCompletionService(this.execService);

      // Submit all jobs and save Future objects
      this.jobsForExec.stream().forEach(job ->
          futureList.add(execCompletionService.submit(() -> job.execute(), null)));

      // Jobs completion monitoring
      for (int i = 0; i < this.jobsForExec.size(); i++) {
        try {
          // Wait and Check the result of finished jobs immediately.
          execCompletionService.take().get();
        } catch (ExecutionException e) {
          // Exception in provided job: log and re-throw
          System.err.println(e.getMessage());
          throw new RuntimeException(e);
        } catch (InterruptedException e) {
          System.err.println("ExecutorJob is interrupted!");
        }
      }
    } finally {
      futureList.stream().forEach(future -> future.cancel(true));
    }
  }
}
