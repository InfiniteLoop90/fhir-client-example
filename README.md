# fhir-client-example: FHIR Client Example

The `fhir-client-example` demonstrates very basic functionality using the HAPI FHIR library to handle the client-side
to doing a search for patients against a FHIR server, along with some rudimentary error handling.

## System requirements

Maven and JDK 8.

## How to build

1. Build the Maven project:

    
    `mvn clean package`

## Run the search against a FHIR server

Once the project is built, it can be ran using the Exec Maven plugin (`exec`).
A FHIR base server URL (e.g., `http://fhirtest.uhn.ca/baseDstu2` or `http://localhost:8080/fhir-server-example/rest`) must be provided as a command line argument.
Also (optionally), the log4j logging level can be changed from it's default of `INFO` via the `-Dorg.slf4j.simpleLogger.defaultLogLevel` property.

For example, to run it against a FHIR server with a logging level of `DEBUG`, run the following:

    `mvn exec:java -Dexec.args="http://fhirtest.uhn.ca/baseDstu2" -Dorg.slf4j.simpleLogger.defaultLogLevel=DEBUG`
