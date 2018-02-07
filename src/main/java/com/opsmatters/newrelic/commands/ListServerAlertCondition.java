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
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.servers.Server;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;

/**
 * Implements the New Relic command line option to list the alert conditions for a server.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListServerAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListServerAlertCondition.class.getName());
    private static final String NAME = "list_server_alert_conditions";

    private Long serverId;

    /**
     * Default constructor.
     */
    public ListServerAlertCondition()
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
        options.addOption("si", "server_id", true, "The id of the server");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Server id option
        if(cli.hasOption("si"))
        {
            serverId = Long.parseLong(cli.getOptionValue("si"));
            logOptionValue("server_id", serverId);
        }
        else
        {
            logOptionMissing("server_id");
        }
    }

    /**
     * List the alert conditions for the server.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Getting server: "+serverId);

        Optional<Server> server = Optional.absent();
        try
        {
            server = api.servers().show(serverId);
        }
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!server.isPresent())
        {
            logger.severe("Unable to find server: "+serverId);
            return;
        }

        Server s = server.get();

        if(verbose)
            logger.info("Getting alert conditions for server: "+s.getId());
        Collection<AlertCondition> conditions = api.alertEntityConditions().list(s);
        if(verbose)
            logger.info("Found "+conditions.size()+" alert conditions");
        for(AlertCondition condition : conditions)
            logger.info(condition.getId()+" - "+condition.getName()+" ("+condition.getType()+")");
    }
}