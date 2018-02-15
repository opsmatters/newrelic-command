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

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.channels.AlertChannel;

/**
 * Implements the New Relic command line option to delete alert channels.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteAlertChannels extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteAlertChannels.class.getName());
    private static final String NAME = "delete_alert_channels";

    private String name;

    /**
     * Default constructor.
     */
    public DeleteAlertChannels()
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
        addOption(Opt.NAME, "The name of the alert channels");
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
    }

    /**
     * Delete the alert channels.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting alert channels: "+name);
        Collection<AlertChannel> channels = api.alertChannels().list(name);
        if(channels.size() == 0)
        {
            logger.severe("Unable to find alert channels: "+name);
            return;
        }

        if(verbose())
            logger.info("Deleting "+channels.size()+" alert channels: "+name);

        for(AlertChannel channel : channels)
        {
            api.alertChannels().delete(channel.getId());
            logger.info("Deleted alert channel: "+channel.getId()+" - "+channel.getName());
        }
    }
}