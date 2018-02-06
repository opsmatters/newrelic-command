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
import com.opsmatters.newrelic.api.model.alerts.Priority;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.SyntheticsAlertCondition;

/**
 * Implements the New Relic command line option to create an Synthetics alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateSyntheticsAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateSyntheticsAlertCondition.class.getName());
    private static final String NAME = "create_synthetics_alert_condition";

    private String name;
    private long policyId;
    private String monitorId;

    /**
     * Default constructor.
     */
    public CreateSyntheticsAlertCondition()
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
        options.addOption("n", "name", true, "The name of the alert condition");
        options.addOption("pi", "policy_id", true, "The id of the policy for the alert condition");
        options.addOption("mi", "monitor_id", true, "The id of the Synthetics monitor");
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

        // Monitor id option
        if(cli.hasOption("mi"))
        {
            monitorId = cli.getOptionValue("mi");
            logOptionValue("monitor_id", monitorId);
        }
        else
        {
            logOptionMissing("monitor_id");
        }
    }

    /**
     * Create the alert condition.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating Synthetics alert condition: "+name);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        SyntheticsAlertCondition c = SyntheticsAlertCondition.builder()
            .name(name)
            .monitorId(monitorId)
            .enabled(true)
            .build();

        SyntheticsAlertCondition condition = api.syntheticsAlertConditions().create(policy.get().getId(), c).get();
        logger.info("Created Synthetics alert condition: "+condition.getId()+" - "+condition.getName());
    }
}