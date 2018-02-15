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

/**
 * Implements the New Relic command line option to delete an application.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteApplication extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteApplication.class.getName());
    private static final String NAME = "delete_application";

    private Long id;

    /**
     * Default constructor.
     */
    public DeleteApplication()
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
        addOption(Opt.ID, "The id of the application");
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
            id = Long.parseLong(getOptionValue(cli, Opt.ID));
            logOptionValue(Opt.ID, id);
        }
    }

    /**
     * Delete the application.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting application: "+id);

        Optional<Application> application = Optional.absent();
        try
        {
            application = api.applications().show(id);
        }
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!application.isPresent())
        {
            logger.severe("Unable to find application: "+id);
            return;
        }

        if(verbose())
            logger.info("Deleting  application: "+id);

        Application a = application.get();
        api.applications().delete(a.getId());
        logger.info("Deleted application: "+a.getId()+" - "+a.getName());
    }
}