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

package com.opsmatters.newrelic.commands.alerts.channels;

import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.channels.AlertChannel;
import com.opsmatters.newrelic.api.model.alerts.channels.VictorOpsChannel;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

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
        addOption(Opt.NAME, "The name of the alert channel");
        addOption(Opt.KEY, "The key for the VictorOps channel");
        addOption(Opt.ROUTE_KEY);
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

        // Key option
        if(hasOption(cli, Opt.KEY, true))
        {
            key = getOptionValue(cli, Opt.KEY);
            logOptionValue(Opt.KEY, key);
        }

        // Route key option
        if(hasOption(cli, Opt.ROUTE_KEY, true))
        {
            routeKey = getOptionValue(cli, Opt.ROUTE_KEY);
            logOptionValue(Opt.ROUTE_KEY, routeKey);
        }
    }

    /**
     * Create the VictorOps alert channel.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
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