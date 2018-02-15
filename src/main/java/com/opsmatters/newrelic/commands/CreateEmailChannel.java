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
 * Implements the New Relic command line option to create an Email alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateEmailChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateEmailChannel.class.getName());
    private static final String NAME = "create_email_channel";

    private String name;
    private String recipients;
    private boolean includeJsonAttachment = false;

    /**
     * Default constructor.
     */
    public CreateEmailChannel()
    {
        options();
    }

    /**
     * Returns the name of the command.
     * @return The name of the command
     */
    public String getName()
    {
        return NAME;
    }

    /**
     * Sets the options for the command.
     */
    @Override
    protected void options()
    {
        super.options();
        addOption(Opt.NAME, "The name of the alert channel");
        addOption(Opt.RECIPIENTS, "The email recipients of the alert channel");
        addOption(Opt.INCLUDE_JSON_ATTACHMENT);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(hasOption(cli, Opt.NAME, true))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }

        // Recipients option
        if(hasOption(cli, Opt.RECIPIENTS, true))
        {
            recipients = getOptionValue(cli, Opt.RECIPIENTS);
            logOptionValue(Opt.RECIPIENTS, recipients);
        }

        // IncludeJsonAttachment option
        if(hasOption(cli, Opt.INCLUDE_JSON_ATTACHMENT, false))
        {
            includeJsonAttachment = true;
            logOptionValue(Opt.INCLUDE_JSON_ATTACHMENT, includeJsonAttachment);
        }
    }

    /**
     * Create the Email alert channel.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Creating Email channel: "+name);

        EmailChannel c = EmailChannel.builder()
            .name(name)
            .recipients(recipients)
            .includeJsonAttachment(includeJsonAttachment)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created Email channel: "+channel.getId()+" - "+channel.getName());
    }
}