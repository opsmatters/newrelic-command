![opsmatters](https://i.imgur.com/VoLABc1.png)

# New Relic Command Line
[![Build Status](https://travis-ci.org/opsmatters/newrelic-command.svg?branch=master)](https://travis-ci.org/opsmatters/newrelic-command)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command/badge.svg?style=blue)](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command)
[![Javadocs](http://javadoc.io/badge/com.opsmatters/newrelic-command.svg)](http://javadoc.io/doc/com.opsmatters/newrelic-command)

Java library that allows New Relic Monitoring and Alerting configuration operations to be executed from the command line.

## Examples

The following scripts can be found in the "bin" directory of the distribution:
* new_relic_exec.sh (for Linux)
* new_relic_exec.bat (for Windows)

To execute a command:
```
>$ new_relic_exec.sh create_alert_policy -key "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxx" -name my-policy -ip PER_POLICY
```
A message similar to the following is displayed if the command completes successfully:
```
2018-02-05 02:41:40:941 INFO Created alert policy: 187641 - my-policy
```

To see the options for a particular command:
```
>$ new_relic_exec.sh create_alert_policy -h
```
This produces a listing similar to:
```
usage: create_alert_policy
 -h,--help                        Prints a usage statement
 -ip,--incident_preference <arg>  The incident preference of the alert policy.
                                  Optional, defaults to PER_POLICY.
 -x,--x_api_key <arg>             The New Relic API key for the account or user
 -n,--name <arg>                  The name of the alert policy
 -v,--verbose                     Enables verbose logging messages
```

To see the complete list of commands supported:
```
>$ new_relic_exec.sh
```

The complete list of commands supported is:

### Alert Channels
* create_campfire_channel
* create_email_channel
* create_hipchat_channel
* create_opsgenie_channel
* create_pagerduty_channel
* create_slack_channel
* create_user_channel
* create_victorops_channel
* delete_alert_channel
* delete_alert_channels
* list_alert_channels

### Alert Policies
* create_alert_policy
* delete_alert_policy
* delete_alert_policies
* list_alert_policies

### Alert Conditions
* create_alert_condition
* create_nrql_alert_condition
* create_infra_metric_alert_condition
* create_infra_host_alert_condition
* create_infra_process_alert_condition
* delete_alert_condition
* delete_alert_conditions
* delete_nrql_alert_condition
* delete_nrql_alert_conditions
* delete_infra_alert_condition
* delete_infra_alert_conditions
* list_alert_conditions
* list_nrql_alert_conditions
* list_infra_alert_conditions

Other commands can be included on request.

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