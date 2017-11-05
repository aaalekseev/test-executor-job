package com.dodax.jobs;

/**
 * Job for some long term processing emulation
 */
public class SomeLongTermJob implements IJob {

  private final int durationInMs;
  /**
   * Execution counters
   */
  private int totalExecCount;
  private int successExecCount;
  private int interruptedExecCount;

  /**
   * Constructs SomeLongTermJob with specified duration
   *
   * @param durationInMs job duration in milliseconds
   */
  public SomeLongTermJob(final int durationInMs) {
    this.durationInMs = durationInMs;
  }

  public int getDurationInMs() {
    return this.durationInMs;
  }

  public int getTotalExecCount() {
    return this.totalExecCount;
  }

  public int getSuccessExecCount() {
    return this.successExecCount;
  }

  public int getInterruptedExecCount() {
    return this.interruptedExecCount;
  }

  @Override
  public void execute() {
    this.totalExecCount++;
    try {
      Thread.sleep(this.durationInMs);
    } catch (InterruptedException e) {
      this.interruptedExecCount++;
      return;
    }
    this.successExecCount++;
  }
}
