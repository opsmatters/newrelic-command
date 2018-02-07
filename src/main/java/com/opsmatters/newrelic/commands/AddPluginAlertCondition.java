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
import com.opsmatters.newrelic.api.model.plugins.Plugin;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;

/**
 * Implements the New Relic command line option to add an alert condition to a plugin.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AddPluginAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(AddPluginAlertCondition.class.getName());
    private static final String NAME = "add_plugin_alert_condition";

    private Long pluginId;
    private Long policyId;
    private Long conditionId;

    /**
     * Default constructor.
     */
    public AddPluginAlertCondition()
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
        options.addOption("pl", "plugin_id", true, "The id of the plugin");
        options.addOption("pi", "policy_id", true, "The id of the alert policy");
        options.addOption("ci", "condition_id", true, "The id of the alert condition");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Plugin id option
        if(cli.hasOption("pl"))
        {
            pluginId = Long.parseLong(cli.getOptionValue("pl"));
            logOptionValue("plugin_id", pluginId);
        }
        else
        {
            logOptionMissing("plugin_id");
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

        // Condition id option
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
     * Add the alert condition to the plugin.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        if(verbose)
            logger.info("Getting plugin: "+pluginId);

        Optional<Plugin> plugin = Optional.absent();
        try
        {
            plugin = api.plugins().show(pluginId, false);
        }
        catch(RuntimeException e)
        {
            // throw 404 if not found
        }

        if(!plugin.isPresent())
        {
            logger.severe("Unable to find plugin: "+pluginId);
            return;
        }

        Plugin pl = plugin.get();

        if(verbose)
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        AlertPolicy p = policy.get();

        if(verbose)
            logger.info("Getting alert condition: "+conditionId);

        Optional<AlertCondition> condition = api.alertConditions().show(p.getId(), conditionId);
        if(!condition.isPresent())
        {
            logger.severe("Unable to find alert condition: "+conditionId);
            return;
        }

        AlertCondition c = condition.get();

        if(verbose)
            logger.info("Adding alert condition "+c.getId()+" to plugin "+pl.getId());
        c = api.alertEntityConditions().add(pl, c.getId()).get();
        logger.info("Added condition: "+c.getId()+" - "+c.getName()+" to plugin: "+pl.getId()+" - "+pl.getName());
    }
}