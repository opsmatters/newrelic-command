![opsmatters](https://i.imgur.com/VoLABc1.png)

# New Relic Command Line
[![Build Status](https://travis-ci.org/opsmatters/newrelic-command.svg?branch=master)](https://travis-ci.org/opsmatters/newrelic-command)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command/badge.svg?style=blue)](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command)
[![Javadocs](http://javadoc.io/badge/com.opsmatters/newrelic-command.svg)](http://javadoc.io/doc/com.opsmatters/newrelic-command)

Java library that allows New Relic Monitoring and Alerting configuration operations to be executed from the command line.

## Examples

TBC

## Prerequisites

A New Relic account with an Admin user.
The user needs to generate an [Admin API Key](https://docs.newrelic.com/docs/apis/rest-api-v2/getting-started/api-keys) 
to provide read-write access via the [New Relic REST APIs](https://api.newrelic.com).
The Admin API Key is referenced in the documentation as the parameter "YOUR_API_KEY".

## Installing

First clone the repository using:
```
>$ git clone https://github.com/opsmatters/newrelic-command.git
>$ cd newrelic-command
```

To compile the source code, run all tests, and generate all artefacts (including sources, javadoc, etc):
```
mvn package 
```

## Running the tests

To execute the unit tests:
```
mvn clean test 
```

The following tests are included:

* TBC

## Deployment

The build artefacts are hosted in The Maven Central Repository. 

Add the following dependency to include the artefact within your project:
```
<dependency>
  <groupId>com.opsmatters</groupId>
  <artifactId>newrelic-command</artifactId>
  <version>0.1.0</version>
</dependency>
```

## Built With

* [newrelic-api](https://github.com/opsmatters/newrelic-api) - Java client library for the New Relic Monitoring and Alerting REST APIs
* [Commons CLI](http://commons.apache.org/proper/commons-cli/) - Provides an API for parsing command line options passed to programs
* [Maven](https://maven.apache.org/) - Dependency Management
* [JUnit](http://junit.org/) - Unit testing framework

## Contributing

Please read [CONTRIBUTING.md](https://www.contributor-covenant.org/version/1/4/code-of-conduct.html) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

This project use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/opsmatters/opsmatters-core/tags). 

## Authors

* **Gerald Curley** - *Initial work* - [opsmatters](https://github.com/opsmatters)

See also the list of [contributors](https://github.com/opsmatters/opsmatters-core/contributors) who participated in this project.

## License

This project is licensed under the terms of the [Apache license 2.0](https://www.apache.org/licenses/LICENSE-2.0.html).

<sub>Copyright (c) 2018 opsmatters</sub>