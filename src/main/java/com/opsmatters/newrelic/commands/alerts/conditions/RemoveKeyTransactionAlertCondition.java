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
import com.opsmatters.newrelic.api.model.transactions.KeyTransaction;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;
import com.opsmatters.newrelic.api.exceptions.ErrorResponseException;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to remove an alert condition from a key transaction.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class RemoveKeyTransactionAlertCondition extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(RemoveKeyTransactionAlertCondition.class.getName());
    private static final String NAME = "remove_key_transaction_alert_condition";

    private Long transactionId;
    private Long policyId;
    private Long conditionId;

    /**
     * Default constructor.
     */
    public RemoveKeyTransactionAlertCondition()
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
        addOption(Opt.TRANSACTION_ID);
        addOption(Opt.POLICY_ID);
        addOption(Opt.CONDITION_ID);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Transaction id option
        if(hasOption(cli, Opt.TRANSACTION_ID, true))
        {
            transactionId = Long.parseLong(getOptionValue(cli, Opt.TRANSACTION_ID));
            logOptionValue(Opt.TRANSACTION_ID, transactionId);
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
     * Remove the alert condition from the key transaction.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting key transaction: "+transactionId);

        Optional<KeyTransaction> transaction = Optional.absent();
        try
        {
            transaction = api.keyTransactions().show(transactionId);
        }
        catch(ErrorResponseException e)
        {
            // throw 404 if not found
        }

        if(!transaction.isPresent())
        {
            logger.severe("Unable to find key transaction: "+transactionId);
            return;
        }

        KeyTransaction t = transaction.get();

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
            logger.info("Removing alert condition "+c.getId()+" from transaction "+t.getId());
        api.alertEntityConditions().remove(t, c.getId());
        logger.info("Removed condition: "+c.getId()+" - "+c.getName()+" from transaction: "+t.getId()+" - "+t.getName());
    }
}