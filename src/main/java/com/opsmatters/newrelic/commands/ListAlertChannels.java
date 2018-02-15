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
import com.opsmatters.newrelic.api.model.alerts.channels.ChannelType;

/**
 * Implements the New Relic command line option to list alert channels.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListAlertChannels extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListAlertChannels.class.getName());
    private static final String NAME = "list_alert_channels";

    private String name;
    private String type;

    /**
     * Default constructor.
     */
    public ListAlertChannels()
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
        addOption(Opt.TYPE, "The type of the alert channels");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(hasOption(cli, Opt.NAME, false))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }

        // Type option
        if(hasOption(cli, Opt.TYPE, false))
        {
            type = getOptionValue(cli, Opt.TYPE);

            // Check the value is valid
            if(ChannelType.contains(type))
                logOptionValue(Opt.TYPE, type);
            else
                logOptionInvalid(Opt.TYPE);
        }
    }

    /**
     * List the alert channels.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting alert channels: "+name+(type != null ? " ("+type+")":""));
        Collection<AlertChannel> channels = api.alertChannels().list(name, type);
        if(verbose())
            logger.info("Found "+channels.size()+" alert channels");
        for(AlertChannel channel : channels)
            logger.info(channel.getId()+" - "+channel.getName()+" ("+channel.getType()+")");
    }
}