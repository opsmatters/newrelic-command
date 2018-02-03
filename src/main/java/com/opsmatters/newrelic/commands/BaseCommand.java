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
     * Constructor that takes a list of arguments.
     * @param args The argument list
     */
    public BaseCommand(String[] args)
    {
        this.args = args;
    }

    /**
     * Set the options.
     */
    public void options()
    {
        options.addOption("h", "help", false, "Prints a usage statement");
        options.addOption("v", "verbose", false, "Enables verbose logging messages");
        options.addOption("a", "api_key", true, "The New Relic API key for the account or user");
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
            if(cli.hasOption("a"))
            {
                apiKey = cli.getOptionValue("a");
                if(verbose)
                    logger.info("Using API key: "+apiKey);
            }
            else
            {
                logger.severe("\"api_key\" option is missing");
                help();
            }

            // Parse command-specific options
            parseOptions(cli);
        }
        catch(ParseException e)
        {
            logger.severe("Error parsing command line: "+e.getClass().getName()+e.getMessage());
            help();
        }

        // Execute the command
        execute();
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected abstract void parseOptions(CommandLine cli);

    /**
     * Execute the command using the command line parameters.
     */
    protected abstract void execute();

    /**
     * Print out the help statement.
     */
    protected void help()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("New Relic Command Line", options);
        System.exit(0);
    }
}