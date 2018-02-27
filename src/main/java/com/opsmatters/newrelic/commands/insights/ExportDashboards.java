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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.model.insights.Dashboard;
import com.opsmatters.newrelic.api.model.insights.DashboardList;
import com.opsmatters.newrelic.batch.DashboardManager;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to export a set of alert policies.
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ExportDashboards extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ExportDashboards.class.getName());
    private static final String NAME = "export_dashboards";

    private String filename;
    private String name;

    /**
     * Default constructor.
     */
    public ExportDashboards()
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
        addOption(Opt.FILE, "The name of the file to export the dashboards to");
        addOption(Opt.NAME, "The name of the dashboards (including wildcards)");
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

        // Name option
        if(hasOption(cli, Opt.NAME, false))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }
    }

    /**
     * Export the dashboards.
     */
    protected void execute()
    {
        DashboardManager manager = new DashboardManager(getApiKey(), verbose());
        List<Dashboard> dashboards = manager.getDashboards(true);
        DashboardList dashboardList = new DashboardList(dashboards);

        try
        {
            manager.writeDashboards(dashboardList.list(name), filename,
                new FileWriter(filename));
        }
        catch(IOException e)
        {
            logger.severe("Unable to write dashboard file: "+e.getClass().getName()+": "+e.getMessage());
        }
    }
}