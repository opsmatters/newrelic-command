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
import com.opsmatters.newrelic.api.model.applications.Application;
import com.opsmatters.newrelic.api.model.servers.Server;
import com.opsmatters.newrelic.api.model.labels.Label;

/**
 * Implements the New Relic command line option to create a application label.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateLabel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateLabel.class.getName());
    private static final String NAME = "create_label";

    private Long applicationId;
    private Long serverId;
    private String category;
    private String name;

    /**
     * Default constructor.
     */
    public CreateLabel()
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
        options.addOption("ai", "application_id", true, "The id of the application");
        options.addOption("si", "server_id", true, "The id of the server");
        options.addOption("c", "category", true, "The category of the label");
        options.addOption("n", "name", true, "The name of the label");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Application ID option
        if(cli.hasOption("ai"))
        {
            applicationId = Long.parseLong(cli.getOptionValue("ai"));
            logOptionValue("application_id", applicationId);
        }

        // Server ID option
        if(cli.hasOption("si"))
        {
            serverId = Long.parseLong(cli.getOptionValue("si"));
            logOptionValue("server_id", serverId);
        }

        // Category option
        if(cli.hasOption("c"))
        {
            category = cli.getOptionValue("c");
            logOptionValue("category", category);
        }
        else
        {
            logOptionMissing("category");
        }

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
    }

    /**
     * Create the label.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(applicationId != null)
        {
            if(verbose)
                logger.info("Getting application: "+applicationId);

            Optional<Application> application = Optional.absent();
            try
            {
                application = api.applications().show(applicationId);
            }
            catch(RuntimeException e)
            {
                // throw 404 if not found
            }
 
            if(!application.isPresent())
            {
                logger.severe("Unable to find application: "+applicationId);
                return;
            }
        }

        if(serverId != null)
        {
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
        }

        if(verbose)
            logger.info("Creating label: "+Label.getKey(category, name));

        Label l = Label.builder()
            .category(category)
            .name(name)
            .addApplicationLink(applicationId != null ? applicationId.longValue() : 0L)
            .addServerLink(serverId != null ? serverId.longValue() : 0L)
            .build();

        Label label = api.labels().create(l).get();
        logger.info("Created label: "+label.getKey());
    }
}