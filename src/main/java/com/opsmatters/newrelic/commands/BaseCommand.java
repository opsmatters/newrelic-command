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
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;

/**
 * Implements the New Relic create alert command line option.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public abstract class BaseCommand
{
    private static final Logger logger = Logger.getLogger(BaseCommand.class.getName());

    private String[] args;
    private Options options = new Options();
    private String apiKey;
    private boolean verbose = false;

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
     * Returns <CODE>true</CODE> if verbose logging is enabled.
     * @return <CODE>true</CODE> if verbose logging is enabled
     */
    protected boolean verbose()
    {
        return verbose;
    }

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
        addOption(Opt.HELP);
        addOption(Opt.VERBOSE);
        addOption(Opt.X_API_KEY);
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
            if(hasOption(cli, Opt.HELP, false))
            {
                help();
            }

            // Verbose option
            if(hasOption(cli, Opt.VERBOSE, false))
            {
                verbose = true;
            }

            // API key option
            if(hasOption(cli, Opt.X_API_KEY, true))
            {
                apiKey = getOptionValue(cli, Opt.X_API_KEY);
                logOptionValue(Opt.X_API_KEY, apiKey);
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
        execute();
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected abstract void parse(CommandLine cli);

    /**
     * Execute the command operation using the command line parameters.
     */
    protected abstract void execute();

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
     * Returns <CODE>true</CODE> if the given option exists.
     * @param cli The parsed command line
     * @param opt The option to be checked
     * @param mandatory <CODE>true</CODE> if the option is mandatory. A missing mandatory field will result in an error.
     * @return <CODE>true</CODE> if the given option exists
     */
    protected boolean hasOption(CommandLine cli, Opt opt, boolean mandatory)
    {
        boolean exists = cli.hasOption(opt.shortName());
        if(mandatory && !exists)
            logOptionMissing(opt);
        return exists;
    }

    /**
     * Add an option.
     * @param opt The option to add
     * @param description The description of the option. Overrides the default description from the option.
     */
    protected void addOption(Opt opt, String description)
    {
        options.addOption(opt.shortName(), opt.longName(), opt.hasArg(), description);
    }

    /**
     * Add an option.
     * @param opt The option to add
     */
    protected void addOption(Opt opt)
    {
        if(opt.description() == null)
            throw new IllegalArgumentException("option \""+opt.longName()+"\" has missing description");
        addOption(opt, opt.description());
    }

    /**
     * Returns the value of an option.
     * @param cli The parsed command line
     * @param opt The option to be returned
     * @return The value of the option
     */
    protected String getOptionValue(CommandLine cli, Opt opt)
    {
        return cli.getOptionValue(opt.shortName());
    }

    /**
     * Log the value of an option.
     * @param opt The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(Opt opt, String value)
    {
        if(verbose)
            logger.info("Using "+opt.longName()+": "+value);
    }

    /**
     * Log the value of an option.
     * @param opt The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(Opt opt, boolean value)
    {
        logOptionValue(opt, Boolean.toString(value));
    }

    /**
     * Log the value of an option.
     * @param opt The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(Opt opt, long value)
    {
        logOptionValue(opt, Long.toString(value));
    }

    /**
     * Log the value of an option.
     * @param opt The option to be logged
     * @param value The value of the option
     */
    protected void logOptionValue(Opt opt, double value)
    {
        logOptionValue(opt, Double.toString(value));
    }

    /**
     * Log a missing option.
     * @param opt The option that is missing
     */
    protected void logOptionMissing(Opt opt)
    {
        logger.severe("\""+opt.longName()+"\" option is missing");
        help();
    }

    /**
     * Log an invalid option.
     * @param opt The option that is invalid
     */
    protected void logOptionInvalid(Opt opt)
    {
        logger.severe("\""+opt.longName()+"\" option is invalid");
        help();
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

    /**
     * Create the Synthetics API client.
     * @return The Synthetics API client
     */
    protected NewRelicSyntheticsApi getSyntheticsApi()
    {
        if(verbose)
            logger.info("Creating Synthetics API client");

        return NewRelicSyntheticsApi.builder()
            .apiKey(apiKey)
            .build();
    }
}