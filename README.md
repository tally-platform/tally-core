# tally [![Build Status](https://travis-ci.com/thomasmchen/tally.svg?branch=master)](https://travis-ci.com/thomasmchen/tally) [![codecov](https://codecov.io/gh/thomasmchen/tally/branch/master/graph/badge.svg)](https://codecov.io/gh/thomasmchen/tally)
Consensus based messaging for Twitch

## Testing
Run linter via:
```
mvn checkstyle:checkstyle -Dcheckstyle.consoleOutput=true
```

Run tests:
> Note: Docker must be running on machine for integration tests to pass

```
mvn test
```

Sample start flow:
```
mvn clean validate
mvn clean compile
mvn checkstyle:checkstyle -Dcheckstyle.consoleOutput=true
mvn clean test
```

## Execution
Run API Service via Maven Plugin
```
mvn spring-boot:run
```

## Packaging
Package Service as Executable .jar
```
mvn package 
```

Build Docker Image of Service
```
mvn install dockerfile:build
```

## Contributing
Please refer to the [Git Flow](https://github.com/thomasmchen/tally/wiki/Git-Flow) document in the Wiki for guidance on contributing.

## Versioning
Tally adheres to Semantic Versioning 2.0.0. Learn more [here](https://semver.org/). <br>
The current version of tally is `0.0.0`

