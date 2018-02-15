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
import com.opsmatters.newrelic.api.model.alerts.conditions.PluginsAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.Term;
import com.opsmatters.newrelic.api.model.alerts.conditions.TimeFunction;
import com.opsmatters.newrelic.api.model.alerts.conditions.Operator;
import com.opsmatters.newrelic.api.model.alerts.conditions.PluginId;

/**
 * Implements the New Relic command line option to create a Plugins alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreatePluginsAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreatePluginsAlertCondition.class.getName());
    private static final String NAME = "create_plugins_alert_condition";

    private String name;
    private long policyId;
    private String metric;
    private String priority;
    private Integer duration;
    private String operator;
    private String timeFunction = TimeFunction.ALL.value();
    private String threshold;
    private String metricDescription;
    private String valueFunction;
    private String pluginId;
    private String guid;

    /**
     * Default constructor.
     */
    public CreatePluginsAlertCondition()
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
        addOption(Opt.METRIC);
        addOption(Opt.METRIC_DESCRIPTION);
        addOption(Opt.PRIORITY);
        addOption(Opt.DURATION, "The duration of the condition, either 5, 10, 15, 30, 60, or 120 minutes");
        addOption(Opt.TIME_FUNCTION);
        addOption(Opt.THRESHOLD);
        addOption(Opt.OPERATOR);
        addOption(Opt.VALUE_FUNCTION, "The value_function of the condition, either \"min\", \"max\", \"average\", \"sample_size\", \"total\" or \"percent\", defaults to \"single_value\"");
        addOption(Opt.PLUGIN_ID);
        addOption(Opt.GUID);
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

        // Metric option
        if(hasOption(cli, Opt.METRIC, true))
        {
            metric = getOptionValue(cli, Opt.METRIC);
            logOptionValue(Opt.METRIC, metric);
        }

        // Metric description option
        if(hasOption(cli, Opt.METRIC_DESCRIPTION, true))
        {
            metricDescription = getOptionValue(cli, Opt.METRIC_DESCRIPTION);
            logOptionValue(Opt.METRIC_DESCRIPTION, metricDescription);
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

        // Value function option
        if(hasOption(cli, Opt.VALUE_FUNCTION, true))
        {
            valueFunction = getOptionValue(cli, Opt.VALUE_FUNCTION);

            // Check the value is valid
            if(PluginsAlertCondition.ValueFunction.contains(valueFunction))
                logOptionValue(Opt.VALUE_FUNCTION, valueFunction);
            else
                logOptionInvalid(Opt.VALUE_FUNCTION);
        }

        // Plugin id option
        if(hasOption(cli, Opt.PLUGIN_ID, true))
        {
            pluginId = getOptionValue(cli, Opt.PLUGIN_ID);
            logOptionValue(Opt.PLUGIN_ID, pluginId);
        }

        // GUID option
        if(hasOption(cli, Opt.GUID, true))
        {
            guid = getOptionValue(cli, Opt.GUID);
            logOptionValue(Opt.GUID, guid);
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
            logger.info("Creating Plugins alert condition: "+name);

        PluginsAlertCondition c = PluginsAlertCondition.builder()
            .name(name)
            .metric(metric)
            .metricDescription(metricDescription)
            .valueFunction(valueFunction)
            .plugin(getPlugin())
            .addTerm(getTerm())
            .enabled(true)
            .build();

        PluginsAlertCondition condition = api.pluginsAlertConditions().create(policy.get().getId(), c).get();
        logger.info("Created Plugins alert condition: "+condition.getId()+" - "+condition.getName());
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
     * Returns the plugin for the alert condition.
     */
    private PluginId getPlugin()
    {
        return PluginId.builder()
            .id(pluginId)
            .guid(guid)
            .build();
    }
}