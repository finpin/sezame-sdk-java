# Java SDK for Sezame REST API

Passwordless multi-factor authentication. 

Unlike password-based solutions that require you to remember just another PIN or password, sezame is  a secure and simple multi-factor authentication solution. You only need the username and your fingerprint on your smartphone to log into any sezame-enabled site. Magic – [Sezame](https://seza.me/) – ENTER SIMPLICITY!.

## Prerequisites

Ensure Java 7 JDK and Maven 3.x are installed and added to the environment path.

## Build

    mvn clean install # execute in main directory

## Run end to end tests

In SezameRestClientTest class remove @Ignore annotation above class declaration.

    mvn test # execute all integration tests
    mvn test -Dtest=SezameRestClientTest#shouldRegisterClientApplication # execute test given by its method name (shouldRegister...)
