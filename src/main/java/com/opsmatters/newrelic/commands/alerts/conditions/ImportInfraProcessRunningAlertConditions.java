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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.model.alerts.policies.AlertPolicy;
import com.opsmatters.newrelic.batch.AlertManager;
import com.opsmatters.newrelic.batch.model.AlertConfiguration;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to import a set of infrastructure process alert conditions.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImportInfraProcessRunningAlertConditions extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ImportInfraProcessRunningAlertConditions.class.getName());
    private static final String NAME = "import_infra_process_conditions";

    private String filename;
    private String worksheet;
    private boolean delete = false;

    /**
     * Default constructor.
     */
    public ImportInfraProcessRunningAlertConditions()
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
        addOption(Opt.FILE, "The name of the file containing alert conditions");
        addOption(Opt.SHEET);
        addOption(Opt.DELETE, "Delete any existing alert condition with that name before creating the new alert condition");
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

        // Delete option
        if(hasOption(cli, Opt.DELETE, false))
        {
            delete = true;
        }
    }

    /**
     * Import the infrastructure process alert conditions.
     */
    protected void execute()
    {
        AlertManager manager = new AlertManager(getApiKey(), verbose());
        AlertConfiguration config = new AlertConfiguration();

        // Get the lists of policies
        List<AlertPolicy> policies = manager.getAlertPolicies();

        try
        {
            // Read the alert conditions
            config.setInfraAlertConditions(manager.readInfraProcessRunningAlertConditions(policies, filename, worksheet, 
                new FileInputStream(filename)));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read infra process alert condition file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Delete the existing alert conditions
        if(delete)
            manager.deleteInfraAlertConditions(config.getInfraProcessRunningAlertConditions());

        // Create the new alert conditions
        manager.createInfraAlertConditions(config.getInfraProcessRunningAlertConditions());
    }
}