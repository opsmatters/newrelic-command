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
import com.opsmatters.newrelic.api.model.alerts.channels.VictorOpsChannel;

/**
 * Implements the New Relic command line option to create a VictorOps alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateVictorOpsChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateVictorOpsChannel.class.getName());
    private static final String NAME = "create_victorops_channel";

    private String name;
    private String key;
    private String routeKey;

    /**
     * Default constructor.
     */
    public CreateVictorOpsChannel()
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
        options.addOption("k", "key", true, "The key for the VictorOps channel");
        options.addOption("r", "route_key", true, "The route key of the VictorOps channel");
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

        // Key option
        if(cli.hasOption("k"))
        {
            key = cli.getOptionValue("k");
            logOptionValue("key", key);
        }
        else
        {
            logOptionMissing("key");
        }

        // Route key option
        if(cli.hasOption("r"))
        {
            routeKey = cli.getOptionValue("r");
            logOptionValue("route_key", routeKey);
        }
        else
        {
            logOptionMissing("route_key");
        }
    }

    /**
     * Create the VictorOps alert channel.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating VictorOps channel: "+name);

        VictorOpsChannel c = VictorOpsChannel.builder()
            .name(name)
            .key(key)
            .routeKey(routeKey)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created VictorOps channel: "+channel.getId()+" - "+channel.getName());
    }
}