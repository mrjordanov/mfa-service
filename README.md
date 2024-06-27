# mfa-service

A microservice designed for generating and managing Multi-Factor Authentication (MFA) codes.
The service provides two endpoints- to generate, send, and verify MFA codes.
Currently, there is no real communication between the service and a mail service,
but this can be implemented in the future if needed. It is built with Spring Boot and uses PostgreSQL for data storage.
The project includes also a dependency which can be used for future production and monitoring
and tracking the application's load and performance.

# Running the application

For local development, [a docker-compose.yml](docker-compose.yml) file is provided to start the application easily.
Additionally, an application-local.properties file is configured for running the application locally.
For local development, the service uses an embedded PostgreSQL database. The connection properties can be found
and modified in the [application-local.properties](src/main/resources/application-local.properties) file to connect
to a different database if needed.
When running the service, ensure the noauth profile is activated. This is because a dependency for security is added
only for future development and will be utilized when the service is deployed in production.