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

import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicSyntheticsApi;
import com.opsmatters.newrelic.api.model.synthetics.Monitor;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to delete a Synthetics monitor.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteMonitor extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteMonitor.class.getName());
    private static final String NAME = "delete_monitor";

    private String id;

    /**
     * Default constructor.
     */
    public DeleteMonitor()
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
        addOption(Opt.ID, "The id of the monitor");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // ID option
        if(hasOption(cli, Opt.ID, true))
        {
            id = getOptionValue(cli, Opt.ID);
            logOptionValue(Opt.ID, id);
        }
    }

    /**
     * Delete the monitor.
     */
    protected void execute()
    {
        NewRelicSyntheticsApi syntheticsApi = getSyntheticsApi();

        Optional<Monitor> monitor = Optional.absent();
        try
        {
            monitor = syntheticsApi.monitors().show(id);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!monitor.isPresent())
        {
            logger.severe("Unable to find monitor: "+id);
            return;
        }

        if(verbose())
            logger.info("Deleting monitor: "+id);

        Monitor m = monitor.get();
        syntheticsApi.monitors().delete(m.getId());
        logger.info("Deleted monitor: "+m.getId()+" - "+m.getName());
    }
}