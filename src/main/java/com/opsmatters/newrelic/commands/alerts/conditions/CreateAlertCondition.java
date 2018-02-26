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
import com.opsmatters.newrelic.api.model.alerts.Priority;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.ApmAppAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.ApmKeyTransactionAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.ApmJvmAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.ServersAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.BrowserAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.MobileAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.Term;
import com.opsmatters.newrelic.api.model.alerts.conditions.TimeFunction;
import com.opsmatters.newrelic.api.model.alerts.conditions.Operator;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to create an alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateAlertCondition.class.getName());
    private static final String NAME = "create_alert_condition";

    private String name;
    private String type;
    private long policyId;
    private String metric;
    private String conditionScope;
    private String priority;
    private Integer violationCloseTimer = AlertCondition.ViolationCloseTimerInterval.HOURS_24.value();
    private Integer duration;
    private String operator;
    private String timeFunction = TimeFunction.ALL.value();
    private String threshold;

    /**
     * Default constructor.
     */
    public CreateAlertCondition()
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
        addOption(Opt.POLICY_ID);
        addOption(Opt.NAME, "The name of the alert condition");
        addOption(Opt.TYPE, "The type of the alert condition");
        addOption(Opt.METRIC);
        addOption(Opt.SCOPE);
        addOption(Opt.PRIORITY);
        addOption(Opt.VIOLATION_CLOSE_TIMER);
        addOption(Opt.DURATION, "The duration of the condition, either 5, 10, 15, 30, 60, or 120 minutes");
        addOption(Opt.TIME_FUNCTION);
        addOption(Opt.THRESHOLD);
        addOption(Opt.OPERATOR);
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

        // Type option
        if(hasOption(cli, Opt.TYPE, true))
        {
            type = getOptionValue(cli, Opt.TYPE);

            // Check the value is valid
            if(AlertCondition.ConditionType.contains(type))
                logOptionValue(Opt.TYPE, type);
            else
                logOptionInvalid(Opt.TYPE);
        }

        // Policy id option
        if(hasOption(cli, Opt.POLICY_ID, true))
        {
            policyId = Long.parseLong(getOptionValue(cli, Opt.POLICY_ID));
            logOptionValue(Opt.POLICY_ID, policyId);
        }

        // Metric option
        if(hasOption(cli, Opt.METRIC, true))
        {
            metric = getOptionValue(cli, Opt.METRIC);
            logOptionValue(Opt.METRIC, metric);
        }

        // Scope option
        if(hasOption(cli, Opt.SCOPE, true))
        {
            conditionScope = getOptionValue(cli, Opt.SCOPE);

            // Check the value is valid
            if(AlertCondition.ConditionScope.contains(conditionScope))
                logOptionValue(Opt.SCOPE, conditionScope);
            else
                logOptionInvalid(Opt.SCOPE);
        }

        // Priority option
        if(hasOption(cli, Opt.PRIORITY, true))
        {
            priority = getOptionValue(cli, Opt.PRIORITY);

            // Check the value is valid
            if(Priority.contains(priority))
                logOptionValue(Opt.PRIORITY, priority);
            else
                logOptionInvalid(Opt.PRIORITY);
        }

        // Violation close timer option
        if(hasOption(cli, Opt.VIOLATION_CLOSE_TIMER, false))
        {
            violationCloseTimer = Integer.parseInt(getOptionValue(cli, Opt.VIOLATION_CLOSE_TIMER));

            // Check the value is valid
            if(AlertCondition.ViolationCloseTimerInterval.contains(violationCloseTimer))
                logOptionValue(Opt.VIOLATION_CLOSE_TIMER, violationCloseTimer);
            else
                logOptionInvalid(Opt.VIOLATION_CLOSE_TIMER);
        }

        // Duration option
        if(hasOption(cli, Opt.DURATION, true))
        {
            duration = Integer.parseInt(getOptionValue(cli, Opt.DURATION));

            // Check the value is valid
            if(Term.Duration.contains(duration))
                logOptionValue(Opt.DURATION, duration);
            else
                logOptionInvalid(Opt.DURATION);
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

        // Threshold option
        if(hasOption(cli, Opt.THRESHOLD, true))
        {
            threshold = getOptionValue(cli, Opt.THRESHOLD);
            logOptionValue(Opt.THRESHOLD, threshold);
        }

        // Operator option
        if(hasOption(cli, Opt.OPERATOR, true))
        {
            operator = getOptionValue(cli, Opt.OPERATOR);

            // Check the value is valid
            if(Operator.contains(operator))
                logOptionValue(Opt.OPERATOR, operator);
            else
                logOptionInvalid(Opt.OPERATOR);
        }
    }

    /**
     * Create the alert condition.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        if(verbose())
            logger.info("Creating alert condition: "+name);

        AlertCondition c = null;
        switch(AlertCondition.ConditionType.fromValue(type))
        {
            case APM_APP:
                c = getApmAppMetricCondition();
                break;
            case APM_KEY_TRANSACTION:
                c = getApmKeyTransactionMetricCondition();
                break;
            case APM_JVM:
                c = getApmJvmMetricCondition();
                break;
            case SERVERS:
                c = getServersMetricCondition();
                break;
            case BROWSER:
                c = getBrowserMetricCondition();
                break;
            case MOBILE:
                c = getMobileMetricCondition();
                break;
        }

        AlertCondition condition = api.alertConditions().create(policy.get().getId(), c).get();
        logger.info("Created alert condition: "+condition.getId()+" - "+condition.getName());
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
     * Returns an alert condition of type "apm_app_metric".
     */
    private AlertCondition getApmAppMetricCondition()
    {
        // Check the metric is valid
        if(!ApmAppAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return ApmAppAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "apm_kt_metric".
     */
    private AlertCondition getApmKeyTransactionMetricCondition()
    {
        // Check the metric is valid
        if(!ApmKeyTransactionAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return ApmKeyTransactionAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "apm_jvm_metric".
     */
    private AlertCondition getApmJvmMetricCondition()
    {
        // Check the metric is valid
        if(!ApmJvmAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return ApmJvmAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "servers_metric".
     */
    private AlertCondition getServersMetricCondition()
    {
        // Check the metric is valid
        if(!ServersAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return ServersAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "browser_metric".
     */
    private AlertCondition getBrowserMetricCondition()
    {
        // Check the metric is valid
        if(!BrowserAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return BrowserAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }

    /**
     * Returns an alert condition of type "mobile_metric".
     */
    private AlertCondition getMobileMetricCondition()
    {
        // Check the metric is valid
        if(!MobileAlertCondition.Metric.contains(metric))
            logOptionInvalid(Opt.METRIC);

        return MobileAlertCondition.builder()
            .name(name)
            .metric(metric)
            .conditionScope(conditionScope)
            .violationCloseTimer(violationCloseTimer)
            .addTerm(getTerm())
            .enabled(true)
            .build();
    }
}