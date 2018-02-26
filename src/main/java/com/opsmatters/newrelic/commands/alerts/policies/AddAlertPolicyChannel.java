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

package com.opsmatters.newrelic.commands.alerts.policies;

import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.channels.AlertChannel;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to add an alert channel to an alert policy.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class AddAlertPolicyChannel extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(AddAlertPolicyChannel.class.getName());
    private static final String NAME = "add_alert_policy_channel";

    private Long policyId;
    private Long channelId;

    /**
     * Default constructor.
     */
    public AddAlertPolicyChannel()
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
        addOption(Opt.CHANNEL_ID);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Policy id option
        if(hasOption(cli, Opt.POLICY_ID, true))
        {
            policyId = Long.parseLong(getOptionValue(cli, Opt.POLICY_ID));
            logOptionValue(Opt.POLICY_ID, policyId);
        }

        // Channel id option
        if(hasOption(cli, Opt.CHANNEL_ID, true))
        {
            channelId = Long.parseLong(getOptionValue(cli, Opt.CHANNEL_ID));
            logOptionValue(Opt.CHANNEL_ID, channelId);
        }
    }

    /**
     * Add the channel to the alert policy.
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
            logger.info("Getting alert channel: "+channelId);

        Optional<AlertChannel> channel = api.alertChannels().show(channelId);
        if(!channel.isPresent())
        {
            logger.severe("Unable to find alert channel: "+channelId);
            return;
        }

        AlertPolicy p = policy.get();
        AlertChannel c = channel.get();
        if(verbose())
            logger.info("Adding alert channel "+c.getId()+" to policy "+p.getId());
        api.alertPolicyChannels().update(p.getId(), c.getId());
        logger.info("Added alert channel: "+c.getId()+" - "+c.getName()+" to policy: "+p.getId()+" - "+p.getName());
    }
}