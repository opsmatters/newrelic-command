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
import com.opsmatters.newrelic.api.model.alerts.conditions.ExternalServiceAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.ApmExternalServiceAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.MobileExternalServiceAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.Term;
import com.opsmatters.newrelic.api.model.alerts.conditions.TimeFunction;
import com.opsmatters.newrelic.api.model.alerts.conditions.Operator;

/**
 * Implements the New Relic command line option to create an external service alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateExternalServiceAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateExternalServiceAlertCondition.class.getName());
    private static final String NAME = "create_external_service_alert_condition";

    private String name;
    private String type;
    private long policyId;
    private String metric;
    private String priority;
    private Integer duration;
    private String operator;
    private String timeFunction = TimeFunction.ALL.value();
    private String threshold;
    private String externalServiceUrl;

    /**
     * Default constructor.
     */
    public CreateExternalServiceAlertCondition()
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
        options.addOption("t", "type", true, "The type of the alert condition");
        options.addOption("pi", "policy_id", true, "The id of the policy for the alert condition");
        options.addOption("m", "metric", true, "The metric of the condition, depends on the type");
        options.addOption("p", "priority", true, "The priority of the condition, either \"critical\" or \"warning\"");
        options.addOption("d", "duration", true, "The duration of the condition, either 5, 10, 15, 30, 60, or 120 minutes");
        options.addOption("tf", "time_function", true, "The time_function of the condition, either \"all\" or \"any\", defaults to \"all\"");
        options.addOption("th", "threshold", true, "The threshold of the condition");
        options.addOption("o", "operator", true, "The operator of the condition, either \"above\", \"below\", or \"equal\"");
        options.addOption("u", "url", true, "The URL of the external service being monitored");
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

        // Type option
        if(cli.hasOption("t"))
        {
            type = cli.getOptionValue("t");

            // Check the value is valid
            if(ExternalServiceAlertCondition.ConditionType.contains(type))
                logOptionValue("type", type);
            else
                logOptionInvalid("type");
        }
        else
        {
            logOptionMissing("type");
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

        // Metric option
        if(cli.hasOption("m"))
        {
            metric = cli.getOptionValue("m");
            logOptionValue("metric", metric);
        }
        else
        {
            logOptionMissing("metric");
        }

        // Priority option
        if(cli.hasOption("p"))
        {
            priority = cli.getOptionValue("p");

            // Check the value is valid
            if(Priority.contains(priority))
                logOptionValue("priority", priority);
            else
                logOptionInvalid("priority");
        }
        else
        {
            logOptionMissing("priority");
        }

        // Duration option
        if(cli.hasOption("d"))
        {
            duration = Integer.parseInt(cli.getOptionValue("d"));

            // Check the value is valid
            if(Term.Duration.contains(duration))
                logOptionValue("duration", duration);
            else
                logOptionInvalid("duration");
        }
        else
        {
            logOptionMissing("duration");
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

        // Threshold option
        if(cli.hasOption("th"))
        {
            threshold = cli.getOptionValue("th");
            logOptionValue("threshold", threshold);
        }
        else
        {
            logOptionMissing("threshold");
        }

        // Operator option
        if(cli.hasOption("o"))
        {
            operator = cli.getOptionValue("o");

            // Check the value is valid
            if(Operator.contains(operator))
                logOptionValue("operator", operator);
            else
                logOptionInvalid("operator");
        }
        else
        {
            logOptionMissing("operator");
        }

        // URL option
        if(cli.hasOption("u"))
        {
            externalServiceUrl = cli.getOptionValue("u");
            logOptionValue("url", externalServiceUrl);
        }
        else
        {
            logOptionMissing("url");
        }
    }

    /**
     * Create the alert condition.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        if(verbose)
            logger.info("Creating external service alert condition: "+name);

        ExternalServiceAlertCondition c = null;
        switch(ExternalServiceAlertCondition.ConditionType.fromValue(type))
        {
            case APM:
                c = getApmExternalServiceCondition();
                break;
            case MOBILE:
                c = getMobileExternalServiceCondition();
                break;
        }

        ExternalServiceAlertCondition condition = api.externalServiceAlertConditions().create(policy.get().getId(), c).get();
        logger.info("Created external service alert condition: "+condition.getId()+" - "+condition.getName());
    }

    /**
     * Returns the term for the alert condition.
     */
    private Term getTerm()
    {
        return Term.builder()
            .duration(duration)
            .priority(priority)
            .operator(operator)
            .timeFunction(timeFunction)
            .threshold(threshold)
            .build();
    }

    /**
     * Returns an alert condition of type "apm_external_service".
     */
    private ExternalServiceAlertCondition getApmExternalServiceCondition()
    {
        // Check the metric is valid
        if(!ApmExternalServiceAlertCondition.Metric.contains(metric))
            logOptionInvalid("metric");

        return ApmExternalServiceAlertCondition.builder()
            .name(name)
            .metric(metric)
            .externalServiceUrl(externalServiceUrl)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "mobile_external_service".
     */
    private ExternalServiceAlertCondition getMobileExternalServiceCondition()
    {
        // Check the metric is valid
        if(!MobileExternalServiceAlertCondition.Metric.contains(metric))
            logOptionInvalid("metric");

        return MobileExternalServiceAlertCondition.builder()
            .name(name)
            .metric(metric)
            .externalServiceUrl(externalServiceUrl)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }
}