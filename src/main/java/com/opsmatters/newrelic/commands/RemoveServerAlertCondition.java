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
import com.opsmatters.newrelic.api.model.servers.Server;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;

/**
 * Implements the New Relic command line option to remove an alert condition from a server.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RemoveServerAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(RemoveServerAlertCondition.class.getName());
    private static final String NAME = "remove_server_alert_condition";

    private Long serverId;
    private Long policyId;
    private Long conditionId;

    /**
     * Default constructor.
     */
    public RemoveServerAlertCondition()
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
        addOption(Opt.SERVER_ID);
        addOption(Opt.POLICY_ID);
        addOption(Opt.CONDITION_ID);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Server ID option
        if(hasOption(cli, Opt.SERVER_ID, true))
        {
            serverId = Long.parseLong(getOptionValue(cli, Opt.SERVER_ID));
            logOptionValue(Opt.SERVER_ID, serverId);
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
     * Remove the alert condition from the server.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting server: "+serverId);

        Optional<Server> server = Optional.absent();
        try
        {
            server = api.servers().show(serverId);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!server.isPresent())
        {
            logger.severe("Unable to find server: "+serverId);
            return;
        }

        Server s = server.get();

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
            logger.info("Removing alert condition "+c.getId()+" from server "+s.getId());
        api.alertEntityConditions().remove(s, c.getId());
        logger.info("Removed condition: "+c.getId()+" - "+c.getName()+" from server: "+s.getId()+" - "+s.getName());
    }
}