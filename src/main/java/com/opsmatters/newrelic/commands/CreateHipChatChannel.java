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
import com.opsmatters.newrelic.api.model.alerts.channels.HipChatChannel;

/**
 * Implements the New Relic command line option to create a HipChat alert channel.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateHipChatChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateHipChatChannel.class.getName());
    private static final String NAME = "create_hipchat_channel";

    private String name;
    private String authToken;
    private String room;

    /**
     * Default constructor.
     */
    public CreateHipChatChannel()
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
        addOption(Opt.AUTH_TOKEN);
        addOption(Opt.ROOM, "The HipChat room");
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

        // Auth token option
        if(hasOption(cli, Opt.AUTH_TOKEN, true))
        {
            authToken = getOptionValue(cli, Opt.AUTH_TOKEN);
            logOptionValue(Opt.AUTH_TOKEN, authToken);
        }

        // Room option
        if(hasOption(cli, Opt.ROOM, true))
        {
            room = getOptionValue(cli, Opt.ROOM);
            logOptionValue(Opt.ROOM, room);
        }
    }

    /**
     * Create the HipChat alert channel.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Creating HipChat channel: "+name);

        HipChatChannel c = HipChatChannel.builder()
            .name(name)
            .authToken(authToken)
            .roomId(room)
            .build();

        AlertChannel channel = api.alertChannels().create(c).get();
        logger.info("Created HipChat channel: "+channel.getId()+" - "+channel.getName());
    }
}