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
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;

/**
 * Implements the New Relic command line option to list alert policies.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListAlertPolicies extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListAlertPolicies.class.getName());
    private static final String NAME = "list_alert_policies";

    private String name;

    /**
     * Default constructor.
     */
    public ListAlertPolicies()
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
        options.addOption("n", "name", true, "The name of the alert policies");
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
    }

    /**
     * List the alert policies.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Getting alert policies: "+name);
        Collection<AlertPolicy> policies = api.alertPolicies().list(name);
        if(verbose)
            logger.info("Found "+policies.size()+" alert policies");
        for(AlertPolicy policy : policies)
            logger.info(policy.getId()+" - "+policy.getName()+" ("+policy.getIncidentPreference()+")");
    }
}