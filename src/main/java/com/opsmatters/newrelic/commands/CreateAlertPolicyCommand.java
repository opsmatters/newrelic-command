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
public class CreateAlertPolicyCommand extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateAlertPolicyCommand.class.getName());

    private String name;
    private String incidentPreference = IncidentPreference.PER_POLICY.name();

    /**
     * Constructor that takes a list of arguments.
     * @param args The argument list
     */
    public CreateAlertPolicyCommand(String[] args)
    {
        super(args);
        options();
    }

    /**
     * Set the options.
     */
    public void options()
    {
        super.options();

        options.addOption("n", "name", true, "The name of the alert policy");
        options.addOption("i", "incident_preference", true, "The incident preference of the alert policy");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parseOptions(CommandLine cli)
    {
        // Name option
        if(cli.hasOption("n"))
        {
            name = cli.getOptionValue("n");
            if(verbose)
                logger.info("Using name: "+name);
        }
        else
        {
            logger.severe("\"name\" option is missing");
            help();
        }

        // Incident preference option
        if(cli.hasOption("i"))
        {
            incidentPreference = cli.getOptionValue("i");
            if(verbose)
                logger.info("Using incident preference: "+incidentPreference);
        }
    }

    /**
     * Create the alert policy.
     */
    protected void execute()
    {
        if(verbose)
            logger.info("Creating REST API client");

        NewRelicApi api = NewRelicApi.builder()
            .apiKey(apiKey)
            .build();

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