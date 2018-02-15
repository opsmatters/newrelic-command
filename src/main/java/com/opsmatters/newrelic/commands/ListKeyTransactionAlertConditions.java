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
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.transactions.KeyTransaction;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;

/**
 * Implements the New Relic command line option to list the alert conditions for a key transaction.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListKeyTransactionAlertConditions extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListKeyTransactionAlertConditions.class.getName());
    private static final String NAME = "list_key_transaction_alert_conditions";

    private Long transactionId;

    /**
     * Default constructor.
     */
    public ListKeyTransactionAlertConditions()
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
    }

    /**
     * List the alert conditions for the key transaction.
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
        catch(RuntimeException e)
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
            logger.info("Getting alert conditions for key transaction: "+t.getId());
        Collection<AlertCondition> conditions = api.alertEntityConditions().list(t);
        if(verbose())
            logger.info("Found "+conditions.size()+" alert conditions");
        for(AlertCondition condition : conditions)
            logger.info(condition.getId()+" - "+condition.getName()+" ("+condition.getType()+")");
    }
}