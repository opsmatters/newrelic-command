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
import com.opsmatters.newrelic.api.model.applications.Application;
import com.opsmatters.newrelic.api.model.deployments.Deployment;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;

/**
 * Implements the New Relic command line option to list application deployments.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListDeployments extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListDeployments.class.getName());
    private static final String NAME = "list_deployments";

    private Long applicationId;

    /**
     * Default constructor.
     */
    public ListDeployments()
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
        addOption(Opt.APPLICATION_ID);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Application ID option
        if(hasOption(cli, Opt.APPLICATION_ID, true))
        {
            applicationId = Long.parseLong(getOptionValue(cli, Opt.APPLICATION_ID));
            logOptionValue(Opt.APPLICATION_ID, applicationId);
        }
    }

    /**
     * List the applications.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting application: "+applicationId);

        Optional<Application> application = Optional.absent();
        try
        {
            application = api.applications().show(applicationId);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!application.isPresent())
        {
            logger.severe("Unable to find application: "+applicationId);
            return;
        }

        Application a = application.get();

        if(verbose())
            logger.info("Getting deployments: "+applicationId);
        Collection<Deployment> deployments = api.deployments().list(applicationId);
        if(verbose())
            logger.info("Found "+deployments.size()+" deployments");
        for(Deployment deployment : deployments)
            logger.info(deployment.getId()+" - "+deployment.getRevision()+" ("+deployment.getDescription()+")");
    }
}