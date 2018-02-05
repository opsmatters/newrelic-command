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
import com.opsmatters.newrelic.api.model.alerts.channels.SlackChannel;

/**
 * Implements the New Relic command line option to create a Slack alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateSlackChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateSlackChannel.class.getName());
    private static final String NAME = "create_slack_channel";

    private String name;
    private String url;
    private String channel;

    /**
     * Default constructor.
     */
    public CreateSlackChannel()
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
        options.addOption("n", "name", true, "The name of the alert channel");
        options.addOption("u", "url", true, "The url for the Slack channel");
        options.addOption("c", "channel", true, "The name of the Slack channel");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(cli.hasOption("n"))
        {
            name = cli.getOptionValue("n");
            logOptionValue("name", name);
        }
        else
        {
            logOptionMissing("name");
        }

        // URL option
        if(cli.hasOption("u"))
        {
            url = cli.getOptionValue("u");
            logOptionValue("url", url);
        }
        else
        {
            logOptionMissing("url");
        }

        // Channel option
        if(cli.hasOption("c"))
        {
            channel = cli.getOptionValue("c");
            logOptionValue("channel", channel);
        }
        else
        {
            logOptionMissing("channel");
        }
    }

    /**
     * Create the Slack alert channel.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating Slack channel: "+name);

        SlackChannel c = SlackChannel.builder()
            .name(name)
            .url(url)
            .channel(channel)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created Slack channel: "+channel.getId()+" - "+channel.getName());
    }
}