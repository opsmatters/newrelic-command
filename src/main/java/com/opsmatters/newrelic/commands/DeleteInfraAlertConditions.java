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
import com.opsmatters.newrelic.api.NewRelicInfraApi;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraAlertCondition;

/**
 * Implements the New Relic command line option to delete Infrastructure alert conditions.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteInfraAlertConditions extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteInfraAlertConditions.class.getName());
    private static final String NAME = "delete_infra_alert_conditions";

    private long policyId;
    private String name;

    /**
     * Default constructor.
     */
    public DeleteInfraAlertConditions()
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
        options.addOption("pi", "policy_id", true, "The id of the policy for the alert conditions");
        options.addOption("n", "name", true, "The name of the alert conditions");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Policy id option
        if(cli.hasOption("pi"))
        {
            policyId = Long.parseLong(cli.getOptionValue("pi"));
            logOptionValue("policy_id", policyId);
        }
        else
        {
            logOptionMissing("policy_id");
        }

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
    }

    /**
     * Delete the alert conditions.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();
        NewRelicInfraApi infraApi = getInfraApi();

        if(verbose)
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        if(verbose)
            logger.info("Getting infra alert conditions: "+name);

        Collection<InfraAlertCondition> conditions = infraApi.infraAlertConditions().list(policyId, name);
        if(conditions.size() == 0)
        {
            logger.severe("Unable to find infra alert conditions: "+name);
            return;
        }

        if(verbose)
            logger.info("Deleting "+conditions.size()+" infra alert conditions: "+name);

        for(InfraAlertCondition condition : conditions)
        {
            infraApi.infraAlertConditions().delete(condition.getId());
            logger.info("Deleted infra alert condition: "+condition.getId()+" - "+condition.getName());
        }
    }
}