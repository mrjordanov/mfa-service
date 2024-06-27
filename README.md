# mfa-service

A microservice designed for generating and managing Multi-Factor Authentication (MFA) codes.
The service provides two endpoints- to generate, send, and verify MFA codes.
Currently, there is no real communication between the service and a mail service,
but this can be implemented in the future if needed. It is built with Spring Boot and uses PostgreSQL for data storage.
The project includes also a dependency which can be used for future production and monitoring
and tracking the application's load and performance.

# Running the application- local development
For local development, the service uses an embedded PostgreSQL database. The connection properties can be found
and modified in the [application-local.properties](src/main/resources/application-local.properties) file to connect
to a different database if needed.
When running the service, ensure the noauth profile is activated. This is because a dependency for security is added
only for future development and will be utilized when the service is deployed in production.

# Running the application- using Docker containers

For local development, a [docker-compose.yml](docker-compose.yml) file is provided to start the application using Docker.
To run the service, execute the following command in the root directory of the project:
- Run docker-compose up --build (Be sure that you are in the root directory of the project) - This will build the Docker
image and start the service with a PostgreSQL database. (The configuration for the database can be found in the
docker-compose.yml file)

And af course be sure that you have docker installed on your machine.