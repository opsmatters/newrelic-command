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
import com.opsmatters.newrelic.api.model.alerts.channels.OpsGenieChannel;

/**
 * Implements the New Relic command line option to create an OpsGenie alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateOpsGenieChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateOpsGenieChannel.class.getName());
    private static final String NAME = "create_opsgenie_channel";

    private String name;
    private String apiKey;
    private String teams;
    private String tags;
    private String recipients;

    /**
     * Default constructor.
     */
    public CreateOpsGenieChannel()
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
        addOption(Opt.API_KEY);
        addOption(Opt.TEAMS);
        addOption(Opt.TAGS);
        addOption(Opt.RECIPIENTS, "The recipients of the OpsGenie channel");
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

        // API key option
        if(hasOption(cli, Opt.API_KEY, true))
        {
            apiKey = getOptionValue(cli, Opt.API_KEY);
            logOptionValue(Opt.API_KEY, apiKey);
        }

        // Teams option
        if(hasOption(cli, Opt.TEAMS, true))
        {
            teams = getOptionValue(cli, Opt.TEAMS);
            logOptionValue(Opt.TEAMS, teams);
        }

        // Tags option
        if(hasOption(cli, Opt.TAGS, true))
        {
            tags = getOptionValue(cli, Opt.TAGS);
            logOptionValue(Opt.TAGS, tags);
        }

        // Recipients option
        if(hasOption(cli, Opt.RECIPIENTS, true))
        {
            recipients = getOptionValue(cli, Opt.RECIPIENTS);
            logOptionValue(Opt.RECIPIENTS, recipients);
        }
    }

    /**
     * Create the OpsGenie alert channel.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Creating OpsGenie channel: "+name);

        OpsGenieChannel c = OpsGenieChannel.builder()
            .name(name)
            .apiKey(apiKey)
            .teams(teams)
            .tags(tags)
            .recipients(recipients)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created OpsGenie channel: "+channel.getId()+" - "+channel.getName());
    }
}