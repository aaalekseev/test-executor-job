package com.dodax.jobs;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by aaalekseev on 15-Oct-16.
 */
public class ExecutorJobTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Test
  public void testExecute_Basic() {
    final int jobCount = 10;
    final int threadCount = 5;
    final List<IJob> jobsForExec = new ArrayList<>();
    for (int i = 0; i < jobCount; i++)
      jobsForExec.add(new SomeLongTermJob(i));

    new ExecutorJob(jobsForExec, threadCount).execute();

    for (int i = 0; i < jobCount; i++)
      Assert.assertEquals(1, ((SomeLongTermJob) jobsForExec.get(i)).getSuccessExecCount());
  }

  @Test(expected = RuntimeException.class)
  public void testExecute_Exception() {
    final List<IJob> jobsForExec = new ArrayList<>();
    jobsForExec.add(new SomeLongTermJob(99999));
    jobsForExec.add(new SomeExceptionJob(100));
    new ExecutorJob(jobsForExec, 2).execute();
  }

  @Test
  public void testExecute_Interruptions() throws InterruptedException {
    final int jobCount = 5;
    final int threadCount = jobCount + 1; // All jobs plus Exceptional
    final List<IJob> jobsForExec = new ArrayList<>();
    for (int i = 0; i < jobCount; i++)
      jobsForExec.add(new SomeLongTermJob(99999));
    jobsForExec.add(new SomeExceptionJob(100));

    try {
      new ExecutorJob(jobsForExec, threadCount).execute();
    } catch (RuntimeException ex) {
    }

    // Wait for interruption completion
    Thread.sleep(100);

    for (int i = 0; i < jobCount; i++)
      Assert.assertEquals(1, ((SomeLongTermJob) jobsForExec.get(i)).getInterruptedExecCount());
  }

  @Test
  public void testExecute_MultiExec() {
    final int jobCount = 10;
    final int threadCount = 5;
    final int execCount = 3;
    final List<IJob> jobsForExec = new ArrayList<>();
    for (int i = 0; i < jobCount; i++)
      jobsForExec.add(new SomeLongTermJob(i));

    final IJob execJob = new ExecutorJob(jobsForExec, threadCount);
    for (int i = 0; i < execCount; i++)
      execJob.execute();

    for (int i = 0; i < jobCount; i++)
      Assert.assertEquals(execCount, ((SomeLongTermJob) jobsForExec.get(i)).getSuccessExecCount());
  }

  @Test
  public void testExecute_MultiThreading() {
    final int jobCount = 10;
    final int threadCount = 5;
    final int jobDurationInMs = 100;
    final List<IJob> jobsForExec = new ArrayList<>();
    for (int i = 0; i < jobCount; i++)
      jobsForExec.add(new SomeLongTermJob(jobDurationInMs));

    final Date startTime = new Date();
    new ExecutorJob(jobsForExec, threadCount).execute();
    final long durationInMs = new Date().getTime() - startTime.getTime();

    Assert.assertTrue(durationInMs <= (1.01 /* One percent gap */ * jobCount * jobDurationInMs) / threadCount);
  }
}
