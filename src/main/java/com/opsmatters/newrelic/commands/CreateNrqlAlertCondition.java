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
import com.opsmatters.newrelic.api.model.alerts.conditions.NrqlAlertCondition;
import com.opsmatters.newrelic.api.model.alerts.conditions.Term;
import com.opsmatters.newrelic.api.model.alerts.conditions.TimeFunction;
import com.opsmatters.newrelic.api.model.alerts.conditions.Operator;
import com.opsmatters.newrelic.api.model.alerts.conditions.Nrql;

/**
 * Implements the New Relic command line option to create an NRQL alert condition.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class CreateNrqlAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(CreateNrqlAlertCondition.class.getName());
    private static final String NAME = "create_nrql_alert_condition";

    private String name;
    private long policyId;
    private String priority;
    private Integer duration;
    private String operator;
    private String timeFunction = TimeFunction.ALL.value();
    private String threshold;
    private String valueFunction = NrqlAlertCondition.ValueFunction.SINGLE_VALUE.value();
    private int sinceValue = 3;
    private String query;

    /**
     * Default constructor.
     */
    public CreateNrqlAlertCondition()
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
        options.addOption("p", "priority", true, "The priority of the condition, either \"critical\" or \"warning\"");
        options.addOption("d", "duration", true, "The duration of the condition, either 5, 10, 15, 30, 60, or 120 minutes");
        options.addOption("tf", "time_function", true, "The time_function of the condition, either \"all\" or \"any\", defaults to \"all\"");
        options.addOption("th", "threshold", true, "The threshold of the condition");
        options.addOption("o", "operator", true, "The operator of the condition, either \"above\", \"below\", or \"equal\"");
        options.addOption("q", "query", true, "The NRQL query for the condition");
        options.addOption("vf", "value_function", true, "The value_function of the condition, either \"single_value\" or \"sum\", defaults to \"single_value\"");
        options.addOption("sv", "since_value", true, "The since_value of the condition in minutes, defaults to 3");
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

        // Query option
        if(cli.hasOption("q"))
        {
            query = cli.getOptionValue("q");
            logOptionValue("query", query);
        }
        else
        {
            logOptionMissing("query");
        }

        // Value function option
        if(cli.hasOption("vf"))
        {
            valueFunction = cli.getOptionValue("vf");

            // Check the value is valid
            if(NrqlAlertCondition.ValueFunction.contains(valueFunction))
                logOptionValue("value_function", valueFunction);
            else
                logOptionInvalid("value_function");
        }

        // Since value option
        if(cli.hasOption("sv"))
        {
            sinceValue = Integer.parseInt(cli.getOptionValue("sv"));
            logOptionValue("since_value", sinceValue);
        }
    }

    /**
     * Create the alert condition.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Creating NRQL alert condition: "+name);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        NrqlAlertCondition c = NrqlAlertCondition.builder()
            .name(name)
            .valueFunction(valueFunction)
            .addTerm(getTerm())
            .nrql(getNrql())
            .enabled(true)
            .build();

        NrqlAlertCondition condition = api.nrqlAlertConditions().create(policy.get().getId(), c).get();
        logger.info("Created NRQL alert condition: "+condition.getId()+" - "+condition.getName());
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
     * Returns the NRQL for the alert condition.
     */
    private Nrql getNrql()
    {
        return Nrql.builder()
            .query(query)
            .sinceValue(sinceValue)
            .build();
    }
}