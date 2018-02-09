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

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;
import com.opsmatters.newrelic.api.model.synthetics.Monitor;
import com.opsmatters.newrelic.api.model.synthetics.ScriptBrowserMonitor;

/**
 * Implements the New Relic command line option to create a Synthetics scripted browser monitor.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateScriptedBrowserMonitor extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateScriptedBrowserMonitor.class.getName());
    private static final String NAME = "create_scripted_browser_monitor";

    private String name;
    private int frequency = 10;
    private double slaThreshold = 7.0;
    private List<String> locations = new ArrayList<String>();

    /**
     * Default constructor.
     */
    public CreateScriptedBrowserMonitor()
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
        options.addOption("n", "name", true, "The name of the monitor");
        options.addOption("f", "frequency", true, "The frequency of the monitor in minutes, either 1, 5, 10, 15, 30, 60, 360, 720, or 1440, defaults to 10 minutes");
        options.addOption("s", "sla_threshold", true, "The SLA threshold of the monitor (Apdex T), defaults to 7.0");
        options.addOption("l", "locations", true, "Comma-separated list of locations");
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

        // SLA threshold option (Apdex T)
        if(cli.hasOption("s"))
        {
            slaThreshold = Double.parseDouble(cli.getOptionValue("s"));
            logOptionValue("sla_threshold", slaThreshold);
        }

        // Frequency option
        if(cli.hasOption("f"))
        {
            frequency = Integer.parseInt(cli.getOptionValue("f"));

            // Check the value is valid
            if(Monitor.Frequency.contains(frequency))
                logOptionValue("frequency", frequency);
            else
                logOptionInvalid("frequency");
        }

        // Locations option
        if(cli.hasOption("l"))
        {
            String[] list = cli.getOptionValue("l").split(",");
            for(String location : list)
                locations.add(location.trim());
            logOptionValue("locations", locations.toString());
        }
        else
        {
            logOptionMissing("locations");
        }
    }

    /**
     * Create the monitor.
     */
    protected void operation()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        if(verbose)
            logger.info("Creating monitor: "+name);

        Monitor sm = ScriptBrowserMonitor.builder()
            .name(name)
            .frequency(frequency)
            .slaThreshold(slaThreshold)
            .locations(locations)
            .status(Monitor.Status.ENABLED)
            .build();

        Monitor monitor = syntheticsApi.monitors().create(sm).get();
        logger.info("Created monitor: "+monitor.getId()+" - "+monitor.getName());
    }
}