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

package com.opsmatters.newrelic.commands.accounts;

import java.util.Collection;
import java.util.logging.Logger;
import org.apache.commons.cli.CommandLine;
import com.opsmatters.newrelic.api.NewRelicApi;
import com.opsmatters.newrelic.api.model.accounts.User;
import com.opsmatters.newrelic.commands.Opt;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Implements the New Relic command line option to list users.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class ListUsers extends BaseCommand
{
    private static final Logger logger = Logger.getLogger(ListUsers.class.getName());
    private static final String NAME = "list_users";

    private String name;
    private String role;

    /**
     * Default constructor.
     */
    public ListUsers()
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
        addOption(Opt.NAME, "The name of the users");
        addOption(Opt.ROLE);
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

        // Role option
        if(hasOption(cli, Opt.ROLE, false))
        {
            role = getOptionValue(cli, Opt.ROLE);

            // Check the value is valid
            if(User.Role.contains(role))
                logOptionValue(Opt.ROLE, role);
            else
                logOptionInvalid(Opt.ROLE);
        }
    }

    /**
     * List the users.
     */
    protected void execute()
    {
        NewRelicApi api = getApi();

        if(verbose())
            logger.info("Getting users: "+name+(role != null ? " ("+role+")":""));
        Collection<User> users = api.users().list(name, role);
        if(verbose())
            logger.info("Found "+users.size()+" users");
        for(User user : users)
            logger.info(user.getId()+" - "+user.getFirstName()+" "+user.getLastName()+" ("+user.getRole()+")");
    }
}