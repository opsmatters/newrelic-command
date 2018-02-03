@echo off
REM ********************************************************************************
REM *                                                                              *
REM *  DESCRIPTION                                                                 *
REM *    Executes a New Relic operation using command line options                 *
REM *                                                                              *
REM *  AUTHOR                                                                      *
REM *    Gerald CURLEY (opsmatters)                                                *
REM *                                                                              *
REM *  DATE                                                                        *
REM *    03/02/2018                                                                *
REM *                                                                              *
REM ********************************************************************************

REM Check for JAVA_HOME being set
if [%JAVA_HOME%]==[] goto error

REM Execute the command
%JAVA_HOME%\bin\java -classpath ..\jar\* com.opsmatters.newrelic.executor.NewRelicExecutor %*
goto :eof

:error
@echo ERROR: JAVA_HOME not set
exit /B 1
