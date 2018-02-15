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
        addOption(Opt.NAME, "The name of the alert channel");
        addOption(Opt.URL, "The url for the Slack channel");
        addOption(Opt.CHANNEL);
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

        // URL option
        if(hasOption(cli, Opt.URL, true))
        {
            url = getOptionValue(cli, Opt.URL);
            logOptionValue(Opt.URL, url);
        }

        // Channel option
        if(hasOption(cli, Opt.CHANNEL, true))
        {
            channel = getOptionValue(cli, Opt.CHANNEL);
            logOptionValue(Opt.CHANNEL, channel);
        }
    }

    /**
     * Create the Slack alert channel.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
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