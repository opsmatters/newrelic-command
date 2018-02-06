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
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.IncidentPreference;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;

/**
 * Implements the New Relic command line option to create an alert policy.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateAlertPolicy extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateAlertPolicy.class.getName());
    private static final String NAME = "create_alert_policy";

    private String name;
    private String incidentPreference = IncidentPreference.PER_POLICY.name();

    /**
     * Default constructor.
     */
    public CreateAlertPolicy()
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
        options.addOption("n", "name", true, "The name of the alert policy");
        options.addOption("ip", "incident_preference", true, "The incident preference of the alert policy. Optional, defaults to PER_POLICY.");
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

        // Incident preference option
        if(cli.hasOption("ip"))
        {
            incidentPreference = cli.getOptionValue("ip");

            // Check the value is valid
            if(IncidentPreference.contains(incidentPreference))
                logOptionValue("incident_preference", incidentPreference);
            else
                logOptionInvalid("incident_preference");
        }
    }

    /**
     * Create the alert policy.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating alert policy: "+name);

        AlertPolicy p = AlertPolicy.builder()
            .name(name)
            .incidentPreference(incidentPreference)
            .build();

        AlertPolicy policy = api.alertPolicies().create(p).get();
        logger.info("Created alert policy: "+policy.getId()+" - "+policy.getName());
    }
}