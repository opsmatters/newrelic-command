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

package com.opsmatters.newrelic.commands.synthetics;

import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;
import com.opsmatters.newrelic.api.model.synthetics.Monitor;
import com.opsmatters.newrelic.api.model.synthetics.ScriptApiMonitor;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to create a Synthetics scripted API monitor.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateScriptedApiMonitor extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateScriptedApiMonitor.class.getName());
    private static final String NAME = "create_scripted_api_monitor";

    private String name;
    private int frequency = 10;
    private double slaThreshold = 7.0;
    private List<String> locations = new ArrayList<String>();

    /**
     * Default constructor.
     */
    public CreateScriptedApiMonitor()
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
        addOption(Opt.NAME, "The name of the monitor");
        addOption(Opt.FREQUENCY);
        addOption(Opt.SLA_THRESHOLD);
        addOption(Opt.LOCATIONS);
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

        // SLA threshold option (Apdex T)
        if(hasOption(cli, Opt.SLA_THRESHOLD, false))
        {
            slaThreshold = Double.parseDouble(getOptionValue(cli, Opt.SLA_THRESHOLD));
            logOptionValue(Opt.SLA_THRESHOLD, slaThreshold);
        }

        // Frequency option
        if(hasOption(cli, Opt.FREQUENCY, false))
        {
            frequency = Integer.parseInt(getOptionValue(cli, Opt.FREQUENCY));

            // Check the value is valid
            if(Monitor.Frequency.contains(frequency))
                logOptionValue(Opt.FREQUENCY, frequency);
            else
                logOptionInvalid(Opt.FREQUENCY);
        }

        // Locations option
        if(hasOption(cli, Opt.LOCATIONS, true))
        {
            String[] list = getOptionValue(cli, Opt.LOCATIONS).split(",");
            for(String location : list)
                locations.add(location.trim());
            logOptionValue(Opt.LOCATIONS, locations.toString());
        }
    }

    /**
     * Create the monitor.
     */
    protected void execute()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        if(verbose())
            logger.info("Creating monitor: "+name);

        Monitor sm = ScriptApiMonitor.builder()
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