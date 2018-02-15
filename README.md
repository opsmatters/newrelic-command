![opsmatters](https://i.imgur.com/VoLABc1.png)

# New Relic Command Line
[![Build Status](https://travis-ci.org/opsmatters/newrelic-command.svg?branch=master)](https://travis-ci.org/opsmatters/newrelic-command)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command/badge.svg?style=blue)](https://maven-badges.herokuapp.com/maven-central/com.opsmatters/newrelic-command)
[![Javadocs](http://javadoc.io/badge/com.opsmatters/newrelic-command.svg)](http://javadoc.io/doc/com.opsmatters/newrelic-command)

Java library that allows New Relic Monitoring and Alerting configuration operations to be executed from a command line.
The library includes over 90 operations covering Alerts, Applications, Key Transactions, Deployments, Servers, Plugins, Monitors and Labels. 
It provides a set of tools to simplify or automate the configuration of New Relic Monitoring and Alerting.

## Examples

The following scripts can be found in the "bin" directory of the distribution:
* new_relic_exec.sh (for Linux)
* new_relic_exec.bat (for Windows)

To execute a command provide the operation required as the **first** parameter:
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
* create_synthetics_alert_condition
* create_external_service_alert_condition
* create_plugins_alert_condition
* create_infra_metric_alert_condition
* create_infra_host_alert_condition
* create_infra_process_alert_condition
* delete_alert_condition
* delete_alert_conditions
* delete_nrql_alert_condition
* delete_nrql_alert_conditions
* delete_synthetics_alert_condition
* delete_synthetics_alert_conditions
* delete_external_service_alert_condition
* delete_external_service_alert_conditions
* delete_plugins_alert_condition
* delete_plugins_alert_conditions
* delete_infra_alert_condition
* delete_infra_alert_conditions
* list_alert_conditions
* list_nrql_alert_conditions
* list_synthetics_alert_conditions
* list_external_service_alert_conditions
* list_plugins_alert_conditions
* list_infra_alert_conditions

### Alert Policy Channels
* add_alert_policy_channel
* remove_alert_policy_channel
* list_alert_policy_channels

### Alert Entity Conditions
* add_application_alert_condition
* add_browser_application_alert_condition
* add_mobile_application_alert_condition
* add_server_alert_condition
* add_key_transaction_alert_condition
* add_plugin_alert_condition
* remove_application_alert_condition
* remove_browser_application_alert_condition
* remove_mobile_application_alert_condition
* remove_server_alert_condition
* remove_key_transaction_alert_condition
* remove_plugin_alert_condition
* list_application_alert_conditions
* list_browser_application_alert_conditions
* list_mobile_application_alert_conditions
* list_server_alert_conditions
* list_key_transaction_alert_conditions
* list_plugin_alert_conditions

### Applications
* delete_application
* list_applications
* list_browser_applications
* list_mobile_applications
* list_key_transactions

### Deployments
* create_deployment
* delete_deployment
* list_deployments

### Monitors
* create_ping_monitor
* create_simple_browser_monitor
* create_scripted_browser_monitor
* create_scripted_api_monitor
* update_monitor_script
* delete_monitor
* list_monitors

### Servers
* delete_server
* list_servers

### Plugins
* list_plugins

### Labels
* create_label
* create_monitor_label
* delete_label
* delete_monitor_label
* list_labels
* list_label_monitors

### Dashboards
* delete_dashboard
* list_dashboards

### Users
* list_users

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
  <version>1.1.0</version>
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