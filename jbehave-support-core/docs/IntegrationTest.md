# Integration test
Integration tests are part of CI pipeline on our CI server.

Flow:
- running unit tests
- start jetty container with sample application [jbehave-support-core-test-app](../../jbehave-support-core-test/README.md)
- run integration tests based on file mask *IT.java (it's automatically run also groovy tests)
- shutdown jetty server

## Local test development
Locally you can run tests from:
- intellij
    - [run test application](../../jbehave-support-core-test/README.md#run-test-application-locally)
    - you can run test as regular jbehave/spock test
- command line
    - run all integration tests
    ```
    cd jbehave-support/jbehave-support-core
    mvn clean verify -Pintegration-test -DskipUnitTests=true
    ```
    - run specific integration tests
    ```
    cd jbehave-support/jbehave-support-core
    mvn clean verify -Pintegration-test -DskipUnitTests=true -Dit.test=SampleStoriesIT
    ```
