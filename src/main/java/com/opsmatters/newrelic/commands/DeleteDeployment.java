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
import com.opsmatters.newrelic.api.model.deployments.Deployment;

/**
 * Implements the New Relic command line option to delete an application deployment.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteDeployment extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteDeployment.class.getName());
    private static final String NAME = "delete_deployment";

    private Long applicationId;
    private Long deploymentId;

    /**
     * Default constructor.
     */
    public DeleteDeployment()
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
        options.addOption("di", "deployment_id", true, "The id of the deployment");
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
        else
        {
            logOptionMissing("application_id");
        }

        // Deployment ID option
        if(cli.hasOption("di"))
        {
            deploymentId = Long.parseLong(cli.getOptionValue("di"));
            logOptionValue("deployment_id", deploymentId);
        }
        else
        {
            logOptionMissing("deployment_id");
        }
    }

    /**
     * Delete the deployment.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

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

        Application a = application.get();

        Optional<Deployment> deployment = api.deployments().show(a.getId(), deploymentId);
        if(!deployment.isPresent())
        {
            logger.severe("Unable to find deployment: "+deploymentId);
            return;
        }

        if(verbose)
            logger.info("Deleting deployment: "+deploymentId);

        Deployment d = deployment.get();
        api.deployments().delete(a.getId(), d.getId());
        logger.info("Deleted deployment: "+d.getId()+" - "+d.getRevision());
    }
}