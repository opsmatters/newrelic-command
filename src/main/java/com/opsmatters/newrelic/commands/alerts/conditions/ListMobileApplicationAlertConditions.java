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

package com.opsmatters.newrelic.commands.alerts.conditions;

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.applications.MobileApplication;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to list the alert conditions for a mobile application.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListMobileApplicationAlertConditions extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListMobileApplicationAlertConditions.class.getName());
    private static final String NAME = "list_mobile_application_alert_conditions";

    private Long applicationId;

    /**
     * Default constructor.
     */
    public ListMobileApplicationAlertConditions()
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
        addOption(Opt.APPLICATION_ID, "The id of the mobile application");
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
     * List the alert conditions for the mobile application.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting mobile application: "+applicationId);

        Optional<MobileApplication> application = Optional.absent();
        try
        {
            application = api.mobileApplications().show(applicationId);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!application.isPresent())
        {
            logger.severe("Unable to find mobile application: "+applicationId);
            return;
        }

        MobileApplication a = application.get();

        if(verbose())
            logger.info("Getting alert conditions for mobile application: "+a.getId());
        Collection<AlertCondition> conditions = api.alertEntityConditions().list(a);
        if(verbose())
            logger.info("Found "+conditions.size()+" alert conditions");
        for(AlertCondition condition : conditions)
            logger.info(condition.getId()+" - "+condition.getName()+" ("+condition.getType()+")");
    }
}