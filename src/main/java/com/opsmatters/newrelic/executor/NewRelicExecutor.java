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

import java.io.IOException;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import com.google.common.reflect.ClassPath;
import com.opsmatters.newrelic.commands.BaseCommand;

/**
 * Processes a New Relic command line execution.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public class NewRelicExecutor
{
    private static Map<String,BaseCommand> commands = new LinkedHashMap<String,BaseCommand>();

    /**
     * Entry point that selects the command to execute.
     * @param args The argument list
     */
    public static void main(String[] args)
    {
        System.setProperty("java.util.logging.config.file","logging.properties");

        // Load all the commands
        loadCommands();

        // Exit if no arguments provided
        if(args.length == 0)
        {
            System.err.println("ERROR: No command provided");
            help();
            System.exit(1);
        }

        // Otherwise execute the command
        String commandName = args[0];
        BaseCommand command = commands.get(commandName);
        if(command != null)
        {
            command.args(args).parse();
        }
        else // Invalid command name
        {
            System.err.println("ERROR: Unknown command: "+commandName);
            help();
            System.exit(1);
        }
    }

    /**
     * Displays the supported operations.
     */
    private static void help()
    {
        System.err.println("The supported commands are:");
        StringBuilder str = new StringBuilder();
        for(BaseCommand command : commands.values())
        {
            if(str.length() > 0)
                str.append("\n");
            str.append("  ");
            str.append(command.getName());
        }
        System.err.println(str.toString());
    }

    /**
     * Load the commands from the package.
     */
    private static void loadCommands()
    {
        try
        {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            Set<ClassPath.ClassInfo> classes = ClassPath.from(loader).getTopLevelClasses("com.opsmatters.newrelic.commands");
            for(ClassPath.ClassInfo ci : classes)
            {
                Class cl = Class.forName(ci.getName());
                if(BaseCommand.class.isAssignableFrom(cl))
                {
                    try
                    {
                        BaseCommand command = BaseCommand.class.cast(cl.newInstance());
                        commands.put(command.getName(), command);
                    }
                    catch(InstantiationException e)
                    {
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        catch(ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        catch(IllegalAccessException e)
        {
            e.printStackTrace();
        }
    }
}