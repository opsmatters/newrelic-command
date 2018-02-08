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
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;
import com.opsmatters.newrelic.api.model.synthetics.Monitor;
import com.opsmatters.newrelic.api.model.labels.Label;

/**
 * Implements the New Relic command line option to create a Synthetics monitor label.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateMonitorLabel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateMonitorLabel.class.getName());
    private static final String NAME = "create_monitor_label";

    private String monitorId;
    private String category;
    private String name;

    /**
     * Default constructor.
     */
    public CreateMonitorLabel()
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
        options.addOption("mi", "monitor_id", true, "The id of the monitor");
        options.addOption("c", "category", true, "The category of the label");
        options.addOption("n", "name", true, "The name of the label");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Monitor ID option
        if(cli.hasOption("mi"))
        {
            monitorId = cli.getOptionValue("mi");
            logOptionValue("monitor_id", monitorId);
        }
        else
        {
            logOptionMissing("monitor_id");
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
     * Create the monitor label.
     */
    protected void operation()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        Optional<Monitor> monitor = Optional.absent();
        try
        {
            monitor = syntheticsApi.monitors().show(monitorId);
        }
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!monitor.isPresent())
        {
            logger.severe("Unable to find monitor: "+monitorId);
            return;
        }

        if(verbose)
            logger.info("Creating label: "+Label.getKey(category, name));

        Label l = Label.builder()
            .category(category)
            .name(name)
            .build();

        Label label = syntheticsApi.monitors().createLabel(monitorId, l).get();
        logger.info("Created label: "+label.getKey());
    }
}