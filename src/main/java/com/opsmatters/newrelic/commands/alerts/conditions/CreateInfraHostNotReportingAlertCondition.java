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

import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.NewRelicInfraApi;
import com.opsmatters.newrelic.api.model.alerts.Priority;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraHostNotReportingAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertThreshold;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to create an Infrastructure host not reporting alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateInfraHostNotReportingAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateInfraHostNotReportingAlertCondition.class.getName());
    private static final String NAME = "create_infra_host_alert_condition";

    private String name;
    private long policyId;
    private Integer duration;
    private String whereClause;

    /**
     * Default constructor.
     */
    public CreateInfraHostNotReportingAlertCondition()
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
        addOption(Opt.NAME, "The name of the alert condition");
        addOption(Opt.POLICY_ID);
        addOption(Opt.DURATION, "The duration of the condition in minutes");
        addOption(Opt.WHERE_CLAUSE);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(hasOption(cli, Opt.NAME, true))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }

        // Policy id option
        if(hasOption(cli, Opt.POLICY_ID, true))
        {
            policyId = Long.parseLong(getOptionValue(cli, Opt.POLICY_ID));
            logOptionValue(Opt.POLICY_ID, policyId);
        }

        // Duration option
        if(hasOption(cli, Opt.DURATION, true))
        {
            duration = Integer.parseInt(getOptionValue(cli, Opt.DURATION));
            logOptionValue(Opt.DURATION, duration);
        }

        // Where clause option
        if(hasOption(cli, Opt.WHERE_CLAUSE, false))
        {
            whereClause = getOptionValue(cli, Opt.WHERE_CLAUSE);
            logOptionValue(Opt.WHERE_CLAUSE, whereClause);
        }
    }

    /**
     * Create the alert condition.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();
        NewRelicInfraApi infraApi = getInfraApi();

        if(verbose())
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        if(verbose())
            logger.info("Creating infra host condition: "+name);

        InfraAlertCondition c = InfraHostNotReportingAlertCondition.builder()
            .policyId(policy.get().getId())
            .name(name)
            .criticalThreshold(new AlertThreshold().builder().durationMinutes(duration).build())
            .whereClause(whereClause)
            .enabled(true)
            .build();

        InfraAlertCondition condition = infraApi.infraAlertConditions().create(c).get();
        logger.info("Created infra host alert condition: "+condition.getId()+" - "+condition.getName());
    }
}