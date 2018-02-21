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
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;

/**
 * Implements the New Relic command line option to remove an alert condition from a plugin.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RemovePluginAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(RemovePluginAlertCondition.class.getName());
    private static final String NAME = "remove_plugin_alert_condition";

    private Long pluginId;
    private Long policyId;
    private Long conditionId;

    /**
     * Default constructor.
     */
    public RemovePluginAlertCondition()
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
        addOption(Opt.PLUGIN_ID);
        addOption(Opt.POLICY_ID);
        addOption(Opt.CONDITION_ID);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Plugin id option
        if(hasOption(cli, Opt.PLUGIN_ID, true))
        {
            pluginId = Long.parseLong(getOptionValue(cli, Opt.PLUGIN_ID));
            logOptionValue(Opt.PLUGIN_ID, pluginId);
        }

        // Policy id option
        if(hasOption(cli, Opt.POLICY_ID, true))
        {
            policyId = Long.parseLong(getOptionValue(cli, Opt.POLICY_ID));
            logOptionValue(Opt.POLICY_ID, policyId);
        }

        // Condition ID option
        if(hasOption(cli, Opt.CONDITION_ID, true))
        {
            conditionId = Long.parseLong(getOptionValue(cli, Opt.CONDITION_ID));
            logOptionValue(Opt.CONDITION_ID, conditionId);
        }
    }

    /**
     * Remove the alert condition from the plugin.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting plugin: "+pluginId);

        Optional<Plugin> plugin = Optional.absent();
        try
        {
            plugin = api.plugins().show(pluginId, false);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!plugin.isPresent())
        {
            logger.severe("Unable to find plugin: "+pluginId);
            return;
        }

        Plugin pl = plugin.get();

        if(verbose())
            logger.info("Getting alert policy: "+policyId);

        Optional<AlertPolicy> policy = api.alertPolicies().show(policyId);
        if(!policy.isPresent())
        {
            logger.severe("Unable to find alert policy: "+policyId);
            return;
        }

        AlertPolicy p = policy.get();

        if(verbose())
            logger.info("Getting alert condition: "+conditionId);

        Optional<AlertCondition> condition = api.alertConditions().show(p.getId(), conditionId);
        if(!condition.isPresent())
        {
            logger.severe("Unable to find alert condition: "+conditionId);
            return;
        }

        AlertCondition c = condition.get();

        if(verbose())
            logger.info("Removing alert condition "+c.getId()+" from plugin "+pl.getId());
        api.alertEntityConditions().remove(pl, c.getId());
        logger.info("Removed condition: "+c.getId()+" - "+c.getName()+" from plugin: "+pl.getId()+" - "+pl.getName());
    }
}