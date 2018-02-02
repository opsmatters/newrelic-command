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

//GERALD
//import java.util.logging.Logger;
//import org.apache.commons.cli.BasicParser;
//import org.apache.commons.cli.CommandLine;
//import org.apache.commons.cli.CommandLineParser;
//import org.apache.commons.cli.HelpFormatter;
//import org.apache.commons.cli.Options;
//import org.apache.commons.cli.ParseException;

/**
 * Implements the New Relic create alert command line option.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateAlertPolicyCommand extends BaseCommand
{
//    private static final Logger logger = Logger.getLogger(CreateAlertPolicyCommand.class.getName());

//    private String[] args = null;
//    private Options options = new Options();

    /**
     * Constructor that takes a list of arguments.
     * @param args The argument list
     */
    public CreateAlertPolicyCommand(String[] args)
    {
        super(args);
        options();
    }

    /**
     * Set the options.
     */
    public void options()
    {
        super.options();
//GERALD: add options
    }

    /**
     * Parse the command-specific options.
     */
    protected void parseOptions()
    {
//GERALD: implement
    }
}