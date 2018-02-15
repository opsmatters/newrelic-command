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
 * Implements the New Relic command line option to create an application deployment.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateDeployment extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateDeployment.class.getName());
    private static final String NAME = "create_deployment";

    private Long applicationId;
    private String revision;
    private String changelog;
    private String description;
    private String user;

    /**
     * Default constructor.
     */
    public CreateDeployment()
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
        addOption(Opt.REVISION);
        addOption(Opt.CHANGELOG);
        addOption(Opt.DESCRIPTION, "The description of the deployment");
        addOption(Opt.USER, "The user of the deployment");
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

        // Revision option
        if(hasOption(cli, Opt.REVISION, true))
        {
            revision = getOptionValue(cli, Opt.REVISION);
            logOptionValue(Opt.REVISION, revision);
        }

        // Changelog option
        if(hasOption(cli, Opt.CHANGELOG, false))
        {
            changelog = getOptionValue(cli, Opt.CHANGELOG);
            logOptionValue(Opt.CHANGELOG, changelog);
        }

        // Description option
        if(hasOption(cli, Opt.DESCRIPTION, false))
        {
            description = getOptionValue(cli, Opt.DESCRIPTION);
            logOptionValue(Opt.DESCRIPTION, description);
        }

        // User option
        if(hasOption(cli, Opt.USER, false))
        {
            user = getOptionValue(cli, Opt.USER);
            logOptionValue(Opt.USER, user);
        }
    }

    /**
     * Create the deployment.
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
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!application.isPresent())
        {
            logger.severe("Unable to find application: "+applicationId);
            return;
        }

        if(verbose())
            logger.info("Creating deployment: "+revision);

        Deployment d = Deployment.builder()
            .revision(revision)
            .changelog(changelog)
            .description(description)
            .user(user)
            .build();

        Application a = application.get();
        Deployment deployment = api.deployments().create(a.getId(), d).get();
        logger.info("Created deployment: "+deployment.getId()+" - "+deployment.getRevision());
    }
}