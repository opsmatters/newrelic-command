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

package com.opsmatters.newrelic.commands;

/**
 * Represents the command line options.  
 * 
 * @author Gerald Curley (opsmatters)
 */
public enum Opt
{
    HELP("h", "help", false, "Prints a usage statement"),
    VERBOSE("v", "verbose", false, "Enables verbose logging messages"),
    X_API_KEY("x", "x_api_key", true, "The New Relic API key for the account or user"),
    ID("i", "id", true),
    NAME("n", "name", true),
    TYPE("t", "type", true),
    DESCRIPTION("d", "description", true),
    POLICY_ID("pi", "policy_id", true, "The id of the alert policy"),
    CHANNEL_ID("ci", "channel_id", true, "The id of the alert channel"),
    INCIDENT_PREFERENCE("ip", "incident_preference", true, "The incident preference of the alert policy, defaults to PER_POLICY"),
    CONDITION_ID("ci", "condition_id", true, "The id of the alert condition"),
    APPLICATION_ID("ai", "application_id", true, "The id of the application"),
    TRANSACTION_ID("ti", "transaction_id", true, "The id of the key transaction"),
    SERVER_ID("si", "server_id", true, "The id of the server"),
    PLUGIN_ID("pl", "plugin_id", true, "The id of the plugin"),
    METRIC("m", "metric", true, "The metric of the condition, depends on the type"),
    METRIC_DESCRIPTION("md", "metric_description", true, "The description of the metric"),
    SCOPE("s", "scope", true, "The scope of the condition, either \"instance\" or \"application\""),
    PRIORITY("p", "priority", true, "The priority of the condition, either \"critical\" or \"warning\""),
    VIOLATION_CLOSE_TIMER("vct", "violation_close_timer", true, "The violation close timer of the condition, either 1, 2, 4, 8, 12, or 24 hours, defaults to 24 hours"),
    DURATION("d", "duration", true),
    TIME_FUNCTION("tf", "time_function", true, "The time_function of the condition, either \"all\" or \"any\", defaults to \"all\""),
    THRESHOLD("th", "threshold", true, "The threshold of the condition"),
    CRITICAL_THRESHOLD("ct", "critical_threshold", true, "The critical threshold of the condition"),
    WARNING_THRESHOLD("wt", "warning_threshold", true, "The warning threshold of the condition, optional"),
    OPERATOR("o", "operator", true, "The operator of the condition, either \"above\", \"below\", or \"equal\""),
    COMPARISON("c", "comparison", true, "The operator of the condition, either \"above\", \"below\", or \"equal\""),
    SUBDOMAIN("s", "subdomain", true, "The subdomain for the Campfire channel"),
    TOKEN("t", "token", true, "The token for the Campfire channel"),
    ROOM("r", "room", true),
    RECIPIENTS("r", "recipients", true),
    INCLUDE_JSON_ATTACHMENT("ija", "include_json_attachment", false, "Include the details with the message as a JSON attachment, defaults to false"),
    AUTH_TOKEN("at", "auth_token", true, "The auth token for the HipChat channel"),
    API_KEY("ak", "api_key", true, "The OpsGenie API key"),
    TEAMS("tm", "teams", true, "The teams for the OpsGenie channel"),
    TAGS("tg", "tags", true, "The tags for the OpsGenie channel"),
    SERVICE_KEY("sk", "service_key", true, "The service key for the PagerDuty channel"),
    URL("u", "url", true),
    CHANNEL("c", "channel", true, "The name of the Slack channel"),
    MONITOR_ID("mi", "monitor_id", true, "The id of the Synthetics monitor"),
    USER("u", "user", true),
    KEY("k", "key", true),
    ROUTE_KEY("rk", "route_key", true, "The route key of the VictorOps channel"),
    DEPLOYMENT_ID("di", "deployment_id", true, "The id of the deployment"),
    ROLE("r", "role", true, "The role of the users, either \"owner\", \"admin\", \"user\" or \"restricted\""),
    CATEGORY("c", "category", true, "The category of the label"),
    WHERE_CLAUSE("wc", "where_clause", true, "The where_clause of the condition, optional"),
    PROCESS_WHERE_CLAUSE("pwc", "process_where_clause", true, "The process_where_clause of the condition, optional"),
    QUERY("q", "query", true, "The NRQL query for the condition"),
    VALUE_FUNCTION("vf", "value_function", true),
    SINCE_VALUE("sv", "since_value", true, "The since_value of the condition in minutes, defaults to 3"),
    GUID("g", "guid", true, "The guid of the plugin"),
    SELECT_VALUE("sv", "select_value", true, "The select_value of the condition"),
    EVENT_TYPE("et", "event_type", true, "The event_type of the condition, either \"SystemSample\", \"StorageSample\", \"ProcessSample\" or \"NetworkSample\""),
    REVISION("r", "revision", true, "The revision of the deployment"),
    CHANGELOG("c", "changelog", true, "The changelog of the deployment"),
    SCRIPT_TEXT("st", "script_text", true, "The text of the monitor script"),
    LOCATIONS("l", "locations", true, "Comma-separated list of locations"),
    URI("u", "uri", true, "The uri of the monitor"),
    FREQUENCY("f", "frequency", true, "The frequency of the monitor in minutes, either 1, 5, 10, 15, 30, 60, 360, 720, or 1440, defaults to 10 minutes"),
    SLA_THRESHOLD("s", "sla_threshold", true, "The SLA threshold of the monitor (Apdex T), defaults to 7.0"),
    VALIDATION_STRING("vs", "validation_string", true, "The validation string for the monitor, optional"),
    VERIFY_SSL("vso", "verify_ssl", true, "Execute an SSL handshake, defaults to false"),
    BYPASS_HEAD_REQUEST("bho", "bypass_head_request", true, "Send full HTTP requests for GET, defaults to true"),
    TREAT_REDIRECT_AS_FAILURE("rfo", "treat_redirect_as_failure", true, "Fail on a HTTP redirect, defaults to false"),
    FILE("f", "file", true, "The name of the file to import/export"),
    SHEET("s", "sheet", true, "For XLS and XLSX files, the name of the worksheet to import/export"),
    DELETE("d", "delete", false, "Delete any existing object with that name before creating the new object"),
    APPEND("a", "append", false, "For export XLS and XLSX files, append the sheet to an existing workbook"),
    POLICY("p", "policy", true, "The name of the alert policy (including wildcards)");

    Opt(String shortOption, String longOption, boolean arg, String description)
    {
        this.shortOption = shortOption;
        this.longOption = longOption;
        this.arg = arg;
        this.description = description;
    }

    Opt(String shortOption, String longOption, boolean arg)
    {
        this(shortOption, longOption, arg, null);
    }

    public String shortName()
    {
        return shortOption;
    }

    public String longName()
    {
        return longOption;
    }

    public boolean hasArg()
    {
        return arg;
    }

    public String description()
    {
        return description;
    }

    private String shortOption;
    private String longOption;
    private boolean arg;
    private String description;
}