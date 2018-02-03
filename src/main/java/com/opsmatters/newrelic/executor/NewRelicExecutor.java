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

package com.opsmatters.newrelic.executor;

import com.opsmatters.newrelic.commands.*;

/**
 * Processes a New Relic command line execution.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NewRelicExecutor
{
    /**
     * Entry point that selects the command to execute.
     * @param args The argument list
     */
    public static void main(String[] args)
    {
        System.setProperty("java.util.logging.config.file","logging.properties");

        // Exit if no arguments provided
        if(args.length == 0)
        {
            System.err.println("ERROR: No command provided");
            System.exit(1);
        }

        // Otherwise execute the command
        String command = args[0];
        switch(command)
        {
            case "create_email_channel":
                new CreateEmailChannelCommand(args).parse();
                break;
            case "create_alert_policy":
                new CreateAlertPolicyCommand(args).parse();
                break;
            default:
                System.err.println("ERROR: Unknown command: "+command);
                System.err.println("The supported commands are:");
                System.err.println("  create_email_channel, create_alert_policy");
                System.exit(1);
        }
    }
}