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

package com.opsmatters.newrelic.commands.insights;

import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.batch.DashboardManager;
import com.opsmatters.newrelic.batch.model.DashboardConfiguration;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to import a set of dashboards.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ImportDashboards extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ImportDashboards.class.getName());
    private static final String NAME = "import_dashboards";

    private String filename;
    private boolean delete = false;

    /**
     * Default constructor.
     */
    public ImportDashboards()
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
        addOption(Opt.FILE, "The name of the file containing dashboards");
        addOption(Opt.DELETE, "Delete any existing dashboard with that name before creating the new dashboard");
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

        // Delete option
        if(hasOption(cli, Opt.DELETE, false))
        {
            delete = true;
        }
    }

    /**
     * Import the dashboards.
     */
    protected void execute()
    {
        DashboardManager manager = new DashboardManager(getApiKey(), verbose());
        DashboardConfiguration config = new DashboardConfiguration();

        try
        {
            // Read the dashboards
            config.setDashboards(manager.readDashboards(filename,
                new FileReader(filename)));
        }
        catch(IOException e)
        {
            logger.severe("Unable to read dashboard file: "+e.getClass().getName()+": "+e.getMessage());
        }

        // Delete the existing dashboards
        if(delete)
            manager.deleteDashboards(config.getDashboards());

        // Create the new dashboards
        manager.createDashboards(config.getDashboards());
    }
}