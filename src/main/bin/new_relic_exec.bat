REM GERALD: banner

rem SET CP=.
rem SET CP=%CP%;newrelic-command-0.1.0.jar
rem SET CP=%CP%;newrelic-api-1.0.2.jar
rem SET CP=%CP%;commons-cli-1.4.jar

REM GERALD: check for JAVA_HOME being set

%JAVA_HOME%\bin\java -classpath ..\jar\* com.opsmatters.newrelic.commands.NewRelicExecutor -v=test