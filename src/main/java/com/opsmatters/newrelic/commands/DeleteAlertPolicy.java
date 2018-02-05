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

import java.util.Collection;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;

/**
 * Implements the New Relic command line option to delete an alert policy.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteAlertPolicy extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteAlertPolicy.class.getName());
    private static final String NAME = "delete_alert_policy";

    private String name;
    private Long id;

    /**
     * Default constructor.
     */
    public DeleteAlertPolicy()
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
        options.addOption("n", "name", true, "The name of the alert policy");
        options.addOption("i", "id", true, "The id of the alert policy");
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

        // ID option
        if(cli.hasOption("i"))
        {
            id = Long.parseLong(cli.getOptionValue("i"));
            logOptionValue("id", id);
        }

        // Check that either the id or name has been provided
        if(name == null && id == null)
        {
            if(name == null)
                logOptionMissing("name");
            if(id == null)
                logOptionMissing("id");
        }
    }

    /**
     * Delete the alert policy.
     */
    protected void operation()
    {
        NewRelicApi api = getApi();

        Collection<AlertPolicy> policies = null;
        if(id == null)
        {
            if(verbose)
                logger.info("Getting alert policies: "+name);
            policies = api.alertPolicies().list(name);
            if(policies != null && policies.size() > 0)
            {
                if(verbose)
                    logger.info("Found "+policies.size()+" alert policie(s)");
            }
        }
        else
        {
            if(verbose)
                logger.info("Getting alert policy: "+id);
            AlertPolicy policy = api.alertPolicies().show(id).get();
            policies = new ArrayList<AlertPolicy>();
            if(verbose)
                logger.info("Found alert policy: "+policy.getId()+" - "+policy.getName());
            policies.add(policy);
        }

        if(policies == null || policies.size() == 0)
        {
            logger.severe("Unable to find alert policies: "+(id != null ? id : name));
            return;
        }

        if(verbose)
            logger.info("Deleting "+policies.size()+" alert policie(s): "+(id != null ? id : name));

        for(AlertPolicy policy : policies)
        {
            api.alertPolicies().delete(policy.getId());
            logger.info("Deleted alert policy: "+policy.getId()+" - "+policy.getName());
        }
    }
}