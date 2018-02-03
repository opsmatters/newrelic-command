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
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.channels.AlertChannel;
import com.opsmatters.newrelic.api.model.alerts.channels.EmailChannel;

/**
 * Implements the New Relic command line option to create an email alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateEmailChannelCommand extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateEmailChannelCommand.class.getName());

    private String name;
    private String recipients;
    private boolean includeJsonAttachment = false;

    /**
     * Constructor that takes a list of arguments.
     * @param args The argument list
     */
    public CreateEmailChannelCommand(String[] args)
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

        options.addOption("n", "name", true, "The name of the alert channel");
        options.addOption("r", "recipients", true, "The email recipients of the alert channel");
        options.addOption("i", "include_json_attachment", false, "Include the details with the message as a JSON attachment");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parseOptions(CommandLine cli)
    {
        // Name option
        if(cli.hasOption("n"))
        {
            name = cli.getOptionValue("n");
            if(verbose)
                logger.info("Using name: "+name);
        }
        else
        {
            logger.severe("\"name\" option is missing");
            help();
        }

        // Recipients option
        if(cli.hasOption("r"))
        {
            recipients = cli.getOptionValue("r");
            if(verbose)
                logger.info("Using recipients: "+recipients);
        }
        else
        {
            logger.severe("\"recipients\" option is missing");
            help();
        }

        // IncludeJsonAttachment option
        if(cli.hasOption("i"))
        {
            includeJsonAttachment = true;
            if(verbose)
                logger.info("Using includeJsonAttachment: "+includeJsonAttachment);
        }
    }

    /**
     * Create the email alert channel.
     */
    protected void execute()
    {
        if(verbose)
            logger.info("Creating REST API client");

        NewRelicApi api = NewRelicApi.builder()
            .apiKey(apiKey)
            .build();

        if(verbose)
            logger.info("Creating email channel: "+name);

        EmailChannel c = EmailChannel.builder()
            .name(name)
            .recipients(recipients)
            .includeJsonAttachment(includeJsonAttachment)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created email channel: "+channel.getId()+" - "+channel.getName());
    }
}