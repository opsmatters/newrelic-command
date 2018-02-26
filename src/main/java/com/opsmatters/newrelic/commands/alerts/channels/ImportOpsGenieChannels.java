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

package com.opsmatters.newrelic.commands.alerts.channels;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.batch.AlertManager;
import com.opsmatters.newrelic.batch.model.AlertConfiguration;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to import a set of OpsGenie alert channels.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImportOpsGenieChannels extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ImportOpsGenieChannels.class.getName());
    private static final String NAME = "import_opsgenie_channels";

    private String filename;
    private String worksheet;
    private boolean delete = false;

    /**
     * Default constructor.
     */
    public ImportOpsGenieChannels()
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
        addOption(Opt.FILE, "The name of the file containing alert channels");
        addOption(Opt.SHEET);
        addOption(Opt.DELETE, "For import files, delete any existing alert channel with that name before creating the new alert channel");
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
     * Import the OpsGenie alert channels.
     */
    protected void execute()
    {
        AlertManager manager = new AlertManager(getApiKey());
        AlertConfiguration config = new AlertConfiguration();

        try
        {
            // Read the alert channels
            config.addAlertChannels(manager.readOpsGenieChannels(filename, worksheet, 
                new FileInputStream(filename)));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read alert channel file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Delete the existing alert channels
        if(delete)
            manager.deleteAlertChannels(config.getAlertChannels());

        // Create the new alert channels
        manager.createAlertChannels(config.getAlertChannels());
    }
}