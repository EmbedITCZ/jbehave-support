# Test application
This module contains test application for core integration tests.

Module contains three submodules:
- jbehave-support-core-test-oxm
- jbehave-support-core-test-app
- jbehave-support-core-test-app-domain

## oxm
Module contains jaxb for ws in app module.

## app-domain
Module contains data classes for REST controller.

## app
Module contains very simple spring boot application.
Application exposes H2 database:
- url: jdbc:h2:tcp://localhost:11112/mem:test;MODE=ORACLE
- username: sa
- password: sa

We are running database in server mode without storage (in memory mode).

Application also expose webservice endpoint for webservice testing. Endpoint url is: http://localhost:8080/services/

## run test application locally
You can run test application locally:
- from intellij
    - run class org.jbehavesupport.core.test.app.JbehaveSupportCoreTestApplication
- from command line
    - cd jbehave-support/jbehave-support-core-test/jbehave-support-core-test-app
    - mvn spring-boot:run
