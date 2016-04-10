# Java SDK for Sezame REST API

## Prerequisites

Ensure Java 7 JDK and Maven 3.x are installed and added to the environment path.

## Build

    mvn clean install # execute in main directory

## Run end to end tests

In SezameRestClientTest class remove @Ignore annotation above class declaration.

    mvn test # execute all integration tests
    mvn test -Dtest=SezameRestClientTest#shouldRegisterClientApplication # execute test given by its method name (shouldRegister...)
