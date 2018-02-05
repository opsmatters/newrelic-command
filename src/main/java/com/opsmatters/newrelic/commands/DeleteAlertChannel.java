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
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.channels.AlertChannel;

/**
 * Implements the New Relic command line option to delete an alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteAlertChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteAlertChannel.class.getName());
    private static final String NAME = "delete_alert_channel";

    private Long id;

    /**
     * Default constructor.
     */
    public DeleteAlertChannel()
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
        options.addOption("i", "id", true, "The id of the alert channel");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // ID option
        if(cli.hasOption("i"))
        {
            id = Long.parseLong(cli.getOptionValue("i"));
            logOptionValue("id", id);
        }
        else
        {
            logOptionMissing("id");
        }
    }

    /**
     * Delete the alert channel.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Getting alert channel: "+id);

        Optional<AlertChannel> channel = api.alertChannels().show(id);
        if(!channel.isPresent())
        {
            logger.severe("Unable to find alert channel: "+id);
            return;
        }

        if(verbose)
            logger.info("Deleting  alert channel: "+id);

        AlertChannel c = channel.get();
        api.alertChannels().delete(c.getId());
        logger.info("Deleted alert channel: "+c.getId()+" - "+c.getName());
    }
}