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

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;
import com.opsmatters.newrelic.api.model.synthetics.Monitor;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to list Synthetics monitors.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListMonitors extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListMonitors.class.getName());
    private static final String NAME = "list_monitors";

    private String name;
    private String type;

    /**
     * Default constructor.
     */
    public ListMonitors()
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
        addOption(Opt.NAME, "The name of the monitors");
        addOption(Opt.TYPE, "The type of the monitors, either \"SIMPLE\", \"BROWSER\", \"SCRIPT_BROWSER\" or \"SCRIPT_API\"");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(hasOption(cli, Opt.NAME, false))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }

        // Type option
        if(hasOption(cli, Opt.TYPE, false))
        {
            type = getOptionValue(cli, Opt.TYPE);

            // Check the value is valid
            if(Monitor.MonitorType.contains(type))
                logOptionValue(Opt.TYPE, type);
            else
                logOptionInvalid(Opt.TYPE);
        }
    }

    /**
     * List the monitors.
     */
    protected void execute()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        if(verbose())
            logger.info("Getting monitors: "+name+(type != null ? " ("+type+")":""));
        Collection<Monitor> monitors = syntheticsApi.monitors().list(name, type, 0, 100);
        if(verbose())
            logger.info("Found "+monitors.size()+" monitors");
        for(Monitor monitor : monitors)
            logger.info(monitor.getId()+" - "+monitor.getName()+" ("+monitor.getType()+")");
    }
}