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

    protected String[] args = null;
    protected Options options = new Options();

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
        options.addOption("h", "help", false, "show help.");
        options.addOption("v", "var", true, "Here you can set parameter .");
    }

//GERALD: add to base class
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
//GERALD: fix
                logger.info("Using cli argument -v=" + cli.getOptionValue("v"));
//GERALD: implement
            }
            else
            {
//GERALD: fix
                logger.severe("MIssing v option");
                help();
            }

            // Parse command-specific options
            parseOptions();
        }
        catch(ParseException e)
        {
//GERALD: fix
            logger.severe("Failed to parse comand line properties: "+e.getClass().getName()+e.getMessage());
            help();
        }
    }

    /**
     * Parse the command-specific options.
     */
    protected abstract void parseOptions();

    /**
     * Print out the help statement.
     */
    protected void help()
    {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("Main", options);
        System.exit(0);
    }
}