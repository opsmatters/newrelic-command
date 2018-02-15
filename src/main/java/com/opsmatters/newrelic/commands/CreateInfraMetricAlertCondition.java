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
import com.opsmatters.newrelic.api.model.alerts.Priority;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.InfraMetricAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertThreshold;
import com.opsmatters.newrelic.api.model.alerts.conditions.Operator;
import com.opsmatters.newrelic.api.model.alerts.conditions.TimeFunction;

/**
 * Implements the New Relic command line option to create an Infrastructure metric alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateInfraMetricAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateInfraMetricAlertCondition.class.getName());
    private static final String NAME = "create_infra_metric_alert_condition";

    private String name;
    private long policyId;
    private String eventType;
    private String selectValue;
    private String comparison;
    private String timeFunction = TimeFunction.ALL.value();
    private Integer duration;
    private Integer criticalThreshold;
    private Integer warningThreshold;
    private String whereClause;

    /**
     * Default constructor.
     */
    public CreateInfraMetricAlertCondition()
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
        addOption(Opt.SELECT_VALUE);
        addOption(Opt.EVENT_TYPE);
        addOption(Opt.COMPARISON);
        addOption(Opt.TIME_FUNCTION);
        addOption(Opt.DURATION, "The duration of the condition in minutes");
        addOption(Opt.CRITICAL_THRESHOLD);
        addOption(Opt.WARNING_THRESHOLD);
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

        // Comparison option
        if(hasOption(cli, Opt.COMPARISON, true))
        {
            comparison = getOptionValue(cli, Opt.COMPARISON);

            // Check the value is valid
            if(Operator.contains(comparison))
                logOptionValue(Opt.COMPARISON, comparison);
            else
                logOptionInvalid(Opt.COMPARISON);
        }

        // Select value option
        if(hasOption(cli, Opt.SELECT_VALUE, true))
        {
            selectValue = getOptionValue(cli, Opt.SELECT_VALUE);
            logOptionValue(Opt.SELECT_VALUE, selectValue);
        }

        // Event type option
        if(hasOption(cli, Opt.EVENT_TYPE, true))
        {
            eventType = getOptionValue(cli, Opt.EVENT_TYPE);

            // Check the value is valid
            if(InfraMetricAlertCondition.EventType.contains(eventType))
                logOptionValue(Opt.EVENT_TYPE, eventType);
            else
                logOptionInvalid(Opt.EVENT_TYPE);
        }

        // Time function option
        if(hasOption(cli, Opt.TIME_FUNCTION, false))
        {
            timeFunction = getOptionValue(cli, Opt.TIME_FUNCTION);

            // Check the value is valid
            if(TimeFunction.contains(timeFunction))
                logOptionValue(Opt.TIME_FUNCTION, timeFunction);
            else
                logOptionInvalid(Opt.TIME_FUNCTION);
        }

        // Duration option
        if(hasOption(cli, Opt.DURATION, true))
        {
            duration = Integer.parseInt(getOptionValue(cli, Opt.DURATION));
            logOptionValue(Opt.DURATION, duration);
        }

        // Critical threshold option
        if(hasOption(cli, Opt.CRITICAL_THRESHOLD, true))
        {
            criticalThreshold = Integer.parseInt(getOptionValue(cli, Opt.CRITICAL_THRESHOLD));
            logOptionValue(Opt.CRITICAL_THRESHOLD, criticalThreshold);
        }

        // Warning threshold option
        if(hasOption(cli, Opt.WARNING_THRESHOLD, false))
        {
            warningThreshold = Integer.parseInt(getOptionValue(cli, Opt.WARNING_THRESHOLD));
            logOptionValue(Opt.WARNING_THRESHOLD, warningThreshold);
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
            logger.info("Creating infra metric condition: "+name);

        InfraMetricAlertCondition.Builder builder = InfraMetricAlertCondition.builder()
            .policyId(policy.get().getId())
            .name(name)
            .eventType(eventType)
            .selectValue(selectValue)
            .comparison(comparison)
            .enabled(true);

        if(warningThreshold != null)
            builder = builder.warningThreshold(new AlertThreshold().builder()
                .durationMinutes(duration)
                .value(warningThreshold)
                .timeFunction(timeFunction)
                .build());

        if(criticalThreshold != null)
            builder = builder.criticalThreshold(new AlertThreshold().builder()
                .durationMinutes(duration)
                .value(criticalThreshold)
                .timeFunction(timeFunction)
                .build());

        if(whereClause != null)
            builder = builder.whereClause(whereClause);

        InfraAlertCondition c = builder.build();
        InfraAlertCondition condition = infraApi.infraAlertConditions().create(c).get();
        logger.info("Created infra metric alert condition: "+condition.getId()+" - "+condition.getName());
    }
}