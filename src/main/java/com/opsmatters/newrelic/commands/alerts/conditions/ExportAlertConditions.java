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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.model.Entity;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicyList;
import com.opsmatters.newrelic.api.model.alerts.conditions.AlertCondition;
import com.opsmatters.newrelic.batch.AlertManager;
import com.opsmatters.core.documents.Workbook;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to export a set of alert conditions.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ExportAlertConditions extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ExportAlertConditions.class.getName());
    private static final String NAME = "export_alert_conditions";

    private String filename;
    private String worksheet;
    private boolean append = false;
    private String policy;

    /**
     * Default constructor.
     */
    public ExportAlertConditions()
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
        addOption(Opt.FILE, "The name of the file to export the alert conditions to");
        addOption(Opt.SHEET);
        addOption(Opt.APPEND);
        addOption(Opt.POLICY);
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Filename option
        if(hasOption(cli, Opt.FILE, true))
        {
            filename = getOptionValue(cli, Opt.FILE);
            logOptionValue(Opt.FILE, filename);
        }

        // Sheet option
        if(hasOption(cli, Opt.SHEET, false))
        {
            worksheet = getOptionValue(cli, Opt.SHEET);
            logOptionValue(Opt.SHEET, worksheet);
        }

        // Append option
        if(hasOption(cli, Opt.APPEND, false))
        {
            append = true;
        }

        // Policy option
        if(hasOption(cli, Opt.POLICY, false))
        {
            policy = getOptionValue(cli, Opt.POLICY);
            logOptionValue(Opt.POLICY, policy);
        }
    }

    /**
     * Export the alert conditions.
     */
    protected void execute()
    {
        AlertManager manager = new AlertManager(getApiKey(), verbose());

        // Get the policies and conditions
        List<AlertPolicy> policies = manager.getAlertPolicies();
        AlertPolicyList policyList = new AlertPolicyList(policies);
        List<AlertCondition> conditions = manager.getAlertConditions(policyList.list(policy));

        // Get the list of entities
        List<Entity> entities = new ArrayList<Entity>();
        entities.addAll(manager.getApplications());
        entities.addAll(manager.getServers());

        try
        {
            Workbook workbook = null;
            if(append)
                workbook = Workbook.getWorkbook(new File(filename));
            manager.writeAlertConditions(policies, entities, conditions, filename, worksheet, 
                new FileOutputStream(filename), workbook);
        }
        catch(IOException e)
        {
            logger.severe("Unable to write alert condition file: "+e.getClass().getName()+": "+e.getMessage());
        }
    }
}