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
import com.opsmatters.newrelic.api.model.synthetics.Script;
import com.opsmatters.newrelic.api.model.synthetics.ScriptLocation;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;

/**
 * Implements the New Relic command line option to update a Synthetics scripted monitor to add a script.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class UpdateMonitorScript extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(UpdateMonitorScript.class.getName());
    private static final String NAME = "update_monitor_script";

    private String monitorId;
    private String scriptText;
    private List<ScriptLocation> scriptLocations = new ArrayList<ScriptLocation>();

    /**
     * Default constructor.
     */
    public UpdateMonitorScript()
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
        addOption(Opt.MONITOR_ID);
        addOption(Opt.SCRIPT_TEXT);
        addOption(Opt.LOCATIONS, "Comma-separated list of locations and HMACs");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Monitor ID option
        if(hasOption(cli, Opt.MONITOR_ID, true))
        {
            monitorId = getOptionValue(cli, Opt.MONITOR_ID);
            logOptionValue(Opt.MONITOR_ID, monitorId);
        }

        // Script text option
        if(hasOption(cli, Opt.SCRIPT_TEXT, true))
        {
            scriptText = getOptionValue(cli, Opt.SCRIPT_TEXT);
            logOptionValue(Opt.SCRIPT_TEXT, scriptText);
        }

        // Locations option
        if(hasOption(cli, Opt.LOCATIONS, false))
        {
            // Parse the locations and HMACs
            String[] list = getOptionValue(cli, Opt.LOCATIONS).split(",");
            for(int i = 0; i < list.length; i+=2)
                scriptLocations.add(ScriptLocation.builder().name(list[i].trim()).hmac(list[i+1].trim()).build());
            logOptionValue(Opt.LOCATIONS, scriptLocations.toString());
        }
    }

    /**
     * Update the monitor script.
     */
    protected void execute()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        Optional<Monitor> monitor = Optional.absent();
        try
        {
            monitor = syntheticsApi.monitors().show(monitorId);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!monitor.isPresent())
        {
            logger.severe("Unable to find monitor: "+monitorId);
            return;
        }

        if(verbose())
            logger.info("Updating monitor script: "+monitorId);

        Script script = Script.builder()
            .scriptText(scriptText)
            .scriptLocations(scriptLocations)
            .build();

        Monitor m = monitor.get();
        syntheticsApi.monitors().updateScript(m.getId(), script);
        logger.info("Updated monitor: "+m.getId()+" - "+m.getName());
    }
}