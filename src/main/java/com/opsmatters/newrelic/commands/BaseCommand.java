/*
 * Copyright 2018 Gerald Curley
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.opsmatters.newrelic.commands;

import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.NewRelicInfraApi;

/**
 * Implements the New Relic create alert command line option.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class BaseCommand
{
    private static final Logger logger = Logger.getLogger(BaseCommand.class.getName());

    protected String[] args;
    protected Options options = new Options();
    protected String apiKey;
    protected boolean verbose = false;

    /**
     * Default constructor.
     */
    public BaseCommand()
    {
    }

    /**
     * Returns the name of the command.
     * @return The name of the command
     */
    public abstract String getName();

    /**
     * Sets the list of arguments for the command.
     * @param args The argument list
     * @return This object
     */
    public BaseCommand args(String[] args)
    {
        this.args = args;
        return this;
    }

    /**
     * Sets the default options for the command.
     */
    protected void options()
    {
        options.addOption("h", "help", false, "Prints a usage statement");
        options.addOption("v", "verbose", false, "Enables verbose logging messages");
        options.addOption("x", "x_api_key", true, "The New Relic API key for the account or user");
    }

    /**
     * Parse the command line arguments.
     */
    public void parse()
    {
        CommandLineParser parser = new BasicParser();

        try
        {
            // Parse the common options
            CommandLine cli = parser.parse(options, args);

            // Help option
            if(cli.hasOption("h"))
            {
                help();
            }

            // Verbose option
            if(cli.hasOption("v"))
            {
                verbose = true;
            }

            // API key option
            if(cli.hasOption("x"))
            {
                apiKey = cli.getOptionValue("x");
                logOptionValue("x_api_key", apiKey);
            }
            else
            {
                logger.severe("\"x_api_key\" option is missing");
                help();
            }

            // Parse command-specific options
            parse(cli);
        }
        catch(ParseException e)
        {
            logger.severe("Error parsing command line: "+e.getClass().getName()+e.getMessage());
            help();
        }

        // Execute the command operation
        operation();
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected abstract void parse(CommandLine cli);

    /**
     * Execute the command operation using the command line parameters.
     */
    protected abstract void operation();

    /**
     * Print out the help statement.
     */
    protected void help()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(getName(), options);
        System.exit(0);
    }

    /**
     * Log a missing option.
     * @param option The option that is missing
     */
    protected void logOptionMissing(String option)
    {
        logger.severe("\""+option+"\" option is missing");
        help();
    }

    /**
     * Log an invalid option.
     * @param option The option that is invalid
     */
    protected void logOptionInvalid(String option)
    {
        logger.severe("\""+option+"\" option is invalid");
        help();
    }

    /**
     * Log the value of an option.
     * @param option The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(String option, String value)
    {
        if(verbose)
            logger.info("Using "+option+": "+value);
    }

    /**
     * Log the value of an option.
     * @param option The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(String option, boolean value)
    {
        logOptionValue(option, Boolean.toString(value));
    }

    /**
     * Log the value of an option.
     * @param option The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(String option, long value)
    {
        logOptionValue(option, Long.toString(value));
    }

    /**
     * Create the REST API client.
     * @return The REST API client
     */
    protected NewRelicApi getApi()
    {
        if(verbose)
            logger.info("Creating REST API client");

        return NewRelicApi.builder()
            .apiKey(apiKey)
            .build();
    }

    /**
     * Create the Infrastructure API client.
     * @return The Infrastructure API client
     */
    protected NewRelicInfraApi getInfraApi()
    {
        if(verbose)
            logger.info("Creating Infra API client");

        return NewRelicInfraApi.builder()
            .apiKey(apiKey)
            .build();
    }
}