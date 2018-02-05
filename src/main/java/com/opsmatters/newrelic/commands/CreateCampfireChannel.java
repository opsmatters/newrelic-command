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
import com.opsmatters.newrelic.api.model.alerts.channels.CampfireChannel;

/**
 * Implements the New Relic command line option to create a Campfire alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateCampfireChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateCampfireChannel.class.getName());
    private static final String NAME = "create_campfire_channel";

    private String name;
    private String subdomain;
    private String token;
    private String room;

    /**
     * Default constructor.
     */
    public CreateCampfireChannel()
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
        options.addOption("s", "subdomain", true, "The subdomain for the Campfire channel");
        options.addOption("t", "token", true, "The token for the Campfire channel");
        options.addOption("r", "room", true, "The room for the Campfire channel");
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

        // Subdomain option
        if(cli.hasOption("s"))
        {
            subdomain = cli.getOptionValue("s");
            logOptionValue("subdomain", subdomain);
        }
        else
        {
            logOptionMissing("subdomain");
        }

        // Token option
        if(cli.hasOption("t"))
        {
            token = cli.getOptionValue("t");
            logOptionValue("token", token);
        }
        else
        {
            logOptionMissing("token");
        }

        // Room option
        if(cli.hasOption("r"))
        {
            room = cli.getOptionValue("r");
            logOptionValue("room", room);
        }
        else
        {
            logOptionMissing("room");
        }
    }

    /**
     * Create the Campfire alert channel.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating Campfire channel: "+name);

        CampfireChannel c = CampfireChannel.builder()
            .name(name)
            .subdomain(subdomain)
            .token(token)
            .room(room)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created Campfire channel: "+channel.getId()+" - "+channel.getName());
    }
}