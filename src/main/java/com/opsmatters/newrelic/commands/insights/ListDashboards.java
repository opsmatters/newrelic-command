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

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.insights.Dashboard;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to list Insights dashboards.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListDashboards extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListDashboards.class.getName());
    private static final String NAME = "list_dashboards";

    private String name;

    /**
     * Default constructor.
     */
    public ListDashboards()
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
        addOption(Opt.NAME, "The name of the dashboards");
    }

    /**
     * Parse the command-specific options.
     * @param cli The parsed command line
     */
    protected void parse(CommandLine cli)
    {
        // Name option
        if(hasOption(cli, Opt.NAME, false))
        {
            name = getOptionValue(cli, Opt.NAME);
            logOptionValue(Opt.NAME, name);
        }
    }

    /**
     * List the dashboards.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting dashboards: "+name);
        Collection<Dashboard> dashboards = api.dashboards().list(name);
        if(verbose())
            logger.info("Found "+dashboards.size()+" dashboards");
        for(Dashboard dashboard : dashboards)
            logger.info(dashboard.getId()+" - "+dashboard.getTitle());
    }
}