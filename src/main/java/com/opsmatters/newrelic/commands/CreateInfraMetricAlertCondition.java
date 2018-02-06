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
        options.addOption("n", "name", true, "The name of the alert condition");
        options.addOption("pi", "policy_id", true, "The id of the policy for the alert condition");
        options.addOption("sv", "select_value", true, "The select_value of the condition");
        options.addOption("et", "event_type", true, "The event_type of the condition, either \"SystemSample\", \"StorageSample\", \"ProcessSample\" or \"NetworkSample\"");
        options.addOption("c", "comparison", true, "The operator of the condition, either \"above\", \"below\", or \"equal\"");
        options.addOption("tf", "time_function", true, "The time_function of the condition, either \"all\" or \"any\", defaults to \"all\"");
        options.addOption("d", "duration", true, "The duration of the condition in minutes");
        options.addOption("ct", "critical_threshold", true, "The critical threshold of the condition");
        options.addOption("wt", "warning_threshold", true, "The warning threshold of the condition, optional");
        options.addOption("wc", "where_clause", true, "The where_clause of the condition, optional");
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

        // Comparison option
        if(cli.hasOption("c"))
        {
            comparison = cli.getOptionValue("c");

            // Check the value is valid
            if(Operator.contains(comparison))
                logOptionValue("comparison", comparison);
            else
                logOptionInvalid("comparison");
        }
        else
        {
            logOptionMissing("comparison");
        }

        // Select value option
        if(cli.hasOption("sv"))
        {
            selectValue = cli.getOptionValue("sv");
            logOptionValue("select_value", selectValue);
        }
        else
        {
            logOptionMissing("select_value");
        }

        // Event type option
        if(cli.hasOption("et"))
        {
            eventType = cli.getOptionValue("et");

            // Check the value is valid
            if(InfraMetricAlertCondition.EventType.contains(eventType))
                logOptionValue("event_type", eventType);
            else
                logOptionInvalid("event_type");
        }
        else
        {
            logOptionMissing("event_type");
        }

        // Time function option
        if(cli.hasOption("tf"))
        {
            timeFunction = cli.getOptionValue("tf");

            // Check the value is valid
            if(TimeFunction.contains(timeFunction))
                logOptionValue("time_function", timeFunction);
            else
                logOptionInvalid("time_function");
        }

        // Duration option
        if(cli.hasOption("d"))
        {
            duration = Integer.parseInt(cli.getOptionValue("d"));
            logOptionValue("duration", duration);
        }
        else
        {
            logOptionMissing("duration");
        }

        // Critical threshold option
        if(cli.hasOption("ct"))
        {
            criticalThreshold = Integer.parseInt(cli.getOptionValue("ct"));
            logOptionValue("critical_threshold", criticalThreshold);
        }
        else
        {
            logOptionMissing("critical_threshold");
        }

        // Warning threshold option
        if(cli.hasOption("wt"))
        {
            warningThreshold = Integer.parseInt(cli.getOptionValue("wt"));
            logOptionValue("warning_threshold", warningThreshold);
        }

        // Where clause option
        if(cli.hasOption("wc"))
        {
            whereClause = cli.getOptionValue("wc");
            logOptionValue("where_clause", whereClause);
        }
    }

    /**
     * Create the alert condition.
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