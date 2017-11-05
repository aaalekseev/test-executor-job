## ExecutorJob: IJob for parallel execution of the several IJob-s
ExecutorJob implements IJob interface and provides opportunity of parallel execution of the several IJob-s, provided as an constructor parameter.
Number of threads could be also customized via input parameter of the ExecutorJob constructor. Multiple executions are permitted.

#### Exception processing strategy
Exception processing strategy is uncommon in comparison to Java executors paradigm: job results could influence each other.
When any job throws an exception, all other jobs are cancelled immediately and this exception is rethrown as a result of ExecutorJob execution.

#### Supporting classes
Two supporting implementations of IJob interface are provided for demonstrative and unit-testing needs:
 - **SomeLongTermJob:** emulates some long-term process and calculates execution counters
 - **SomeExceptionJob:** generates runtime exception after some delay

#### Unit-testing
The following unit-tests are provided in ExecutorJobTest class (testExecute_***):
1. Basic: tests basic functionality
2. Exception: verifies proper exception rethrowing
3. Interruptions: checks that all jobs are stopped after an exception in any.
4. MultiExec: tests multiple execution of ExecutorJob
5. MultiThreading: validates reality of multi-threading processing for IJobs

## Building the program
Implementation is based on maven utility and could be built with the following command (from the sources folder):
```
mvn package
```