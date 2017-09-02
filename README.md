# FHIR Client Example

[![Dependency Status](https://www.versioneye.com/user/projects/593c07060fb24f004fc60166/badge.svg?style=flat)](https://www.versioneye.com/user/projects/593c07060fb24f004fc60166)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE.txt)

This `fhir-client-example` project demonstrates very basic functionality using the [HAPI FHIR](http://hapifhir.io/) library to handle the client-side
to doing a search for patients against a FHIR server, along with some rudimentary error handling.

## System requirements

Maven and JDK 8.

## How to build

1. Build the Maven project:

```
mvn clean package
```

## Run the search against a FHIR server

Once the project is built, it can be ran using the Exec Maven plugin (`exec`).
A FHIR base server URL (e.g., `http://fhirtest.uhn.ca/baseDstu2` or `http://localhost:8080/fhir-server-example/rest`) must be provided as a command line argument.
Also (optionally), the log4j logging level can be changed from it's default of `INFO` via the `-Dorg.slf4j.simpleLogger.defaultLogLevel` property.

For example, to build the project and run it against a FHIR server with a logging level of `DEBUG`, run the following:

```
mvn clean package exec:java -Dexec.args="http://localhost:8080/fhir-server-example/rest" -Dorg.slf4j.simpleLogger.log.com.myorganization=DEBUG
```

## License

This FHIR Client Example is licensed under the terms of the [MIT License](LICENSE.txt).