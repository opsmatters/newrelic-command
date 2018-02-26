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

import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.google.common.base.Optional;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.insights.Dashboard;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to delete an Insights dashboard.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class DeleteDashboard extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(DeleteDashboard.class.getName());
    private static final String NAME = "delete_dashboard";

    private Long id;

    /**
     * Default constructor.
     */
    public DeleteDashboard()
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
        addOption(Opt.ID, "The id of the dashboard");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // ID option
        if(hasOption(cli, Opt.ID, true))
        {
            id = Long.parseLong(getOptionValue(cli, Opt.ID));
            logOptionValue(Opt.ID, id);
        }
    }

    /**
     * Delete the dashboard.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        Optional<Dashboard> dashboard = api.dashboards().show(id);
        if(!dashboard.isPresent())
        {
            logger.severe("Unable to find dashboard: "+id);
            return;
        }

        if(verbose())
            logger.info("Deleting dashboard: "+id);

        Dashboard d = dashboard.get();
        api.dashboards().delete(d.getId());
        logger.info("Deleted dashboard: "+d.getId()+" - "+d.getTitle());
    }
}