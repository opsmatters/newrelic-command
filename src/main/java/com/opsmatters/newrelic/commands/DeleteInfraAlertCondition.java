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
import com.opsmatters.newrelic.api.NewRelicInfraApi;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraAlertCondition;

/**
 * Implements the New Relic command line option to delete an Infrastructure alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteInfraAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteInfraAlertCondition.class.getName());
    private static final String NAME = "delete_infra_alert_condition";

    private long policyId;
    private Long conditionId;

    /**
     * Default constructor.
     */
    public DeleteInfraAlertCondition()
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
        options.addOption("pi", "policy_id", true, "The id of the policy for the alert condition");
        options.addOption("ci", "condition_id", true, "The id of the alert condition");
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

        // Condition ID option
        if(cli.hasOption("ci"))
        {
            conditionId = Long.parseLong(cli.getOptionValue("ci"));
            logOptionValue("condition_id", conditionId);
        }
        else
        {
            logOptionMissing("condition_id");
        }
    }

    /**
     * Delete the alert condition.
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
            logger.info("Getting infra alert condition: "+conditionId);

        Optional<InfraAlertCondition> condition = Optional.absent();
        try
        {
            condition = infraApi.infraAlertConditions().show(conditionId);
        }
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!condition.isPresent())
        {
            logger.severe("Unable to find infra alert condition: "+conditionId);
            return;
        }

        if(verbose)
            logger.info("Deleting infra alert condition: "+conditionId);

        InfraAlertCondition c = condition.get();
        infraApi.infraAlertConditions().delete(c.getId());
        logger.info("Deleted infra alert condition: "+c.getId()+" - "+c.getName());
    }
}