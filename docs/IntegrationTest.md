# Integration test
Integration tests are part of CI pipeline on our CI server.

Flow:
- running unit tests
- start sample application [jbehave-support-core-test-app](../jbehave-support-core-test/README.md)
- run integration tests based on file mask *IT.java (it automatically runs also groovy tests)
- stop sample application

## Local test development
Locally you can run tests from:
- intellij
    - [run test application](../jbehave-support-core-test/README.md#run-test-application-locally)
    - you can run test as regular jbehave/spock test
- command line
    - [run test application](../jbehave-support-core-test/README.md#run-test-application-locally)
    - run all integration tests
    ```
    cd jbehave-support/jbehave-support-core
    mvn clean verify -DskipUnitTests=true
    ```
    - run specific integration tests
    ```
    cd jbehave-support/jbehave-support-core
    mvn clean verify -DskipUnitTests=true -Dit.test=SampleStoriesIT
    ```
