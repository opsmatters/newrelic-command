#!/bin/bash

#==============================================================================#
#                                                                              #
# DESCRIPTION                                                                  #
#   Executes a New Relic operation using command line options                  #
#                                                                              #
#==============================================================================#
#                                                                              #
# AUTHOR                                                                       #
#   Gerald CURLEY (opsmatters)                                                 #
#                                                                              #
#==============================================================================#
#                                                                              #
# DATE                                                                         #
#   03/02/2018                                                                 #
#                                                                              #
#==============================================================================#

# Check for JAVA_HOME being set
[ -z "$JAVA_HOME" ] && echo "${JAVA_HOME}: JAVA_HOME not set" && exit 1

# Execute the command
${JAVA_HOME}/bin/java -classpath "../jar/*" com.opsmatters.newrelic.executor.NewRelicExecutor "$@"