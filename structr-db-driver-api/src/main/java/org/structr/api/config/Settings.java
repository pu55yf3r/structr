/*
 * Copyright (C) 2010-2020 Structr GmbH
 *
 * This file is part of Structr <http://structr.org>.
 *
 * Structr is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * Structr is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Structr.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.structr.api.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

/**
 * The Structr configuration settings.
 */
public class Settings {

	public static final String DEFAULT_DATABASE_DRIVER        = "org.structr.memory.MemoryDatabaseService";

	private static final Map<String, Setting> settings        = new LinkedHashMap<>();
	private static final Map<String, SettingsGroup> groups    = new LinkedHashMap<>();

	public static final SettingsGroup generalGroup            = new SettingsGroup("general",     "General Settings");
	public static final SettingsGroup serverGroup             = new SettingsGroup("server",      "Server Settings");
	public static final SettingsGroup databaseGroup           = new SettingsGroup("database",    "Database Configuration");
	public static final SettingsGroup applicationGroup        = new SettingsGroup("application", "Application Configuration");
	public static final SettingsGroup smtpGroup               = new SettingsGroup("smtp",        "Mail Configuration");
	public static final SettingsGroup advancedGroup           = new SettingsGroup("advanced",    "Advanced Settings");
	public static final SettingsGroup servletsGroup           = new SettingsGroup("servlets",    "Servlets");
	public static final SettingsGroup cronGroup               = new SettingsGroup("cron",        "Cron Jobs");
	public static final SettingsGroup securityGroup           = new SettingsGroup("security",    "Security Settings");
	public static final SettingsGroup oauthGroup              = new SettingsGroup("oauth",       "OAuth Settings");
	public static final SettingsGroup ldapGroup               = new SettingsGroup("ldap",        "LDAP Settings");
	public static final SettingsGroup miscGroup               = new SettingsGroup("misc",        "Miscellaneous");

	// general settings
	public static final Setting<String> ReleasesIndexUrl        = new StringSetting(generalGroup,             "Application", "application.releases.index.url",   "https://structr.com/repositories/releases/org/structr/structr/index", "URL with release index (list of version strings for Structr releases)");
	public static final Setting<String> SnapshotsIndexUrl       = new StringSetting(generalGroup,             "Application", "application.snapshots.index.url",  "https://structr.com/repositories/snapshots/org/structr/structr/index", "URL with snapshot index (list of version strings for Structr unstable builds)");
	public static final Setting<String> ApplicationTitle        = new StringSetting(generalGroup,             "Application", "application.title",                "Structr", "The title of the application as shown in the log file. This entry exists for historical reasons and has no functional impact other than appearing in the log file.");
	public static final Setting<String> InstanceName            = new StringSetting(generalGroup,             "Application", "application.instance.name",        "", "The name of the Structr instance (displayed in the top right corner of structr-ui)");
	public static final Setting<String> InstanceStage           = new StringSetting(generalGroup,             "Application", "application.instance.stage",       "", "The stage of the Structr instance (displayed in the top right corner of structr-ui)");
	public static final Setting<String> MenuEntries             = new StringSetting(generalGroup,             "Application", "application.menu.main",            "Dashboard,Pages,Files,Security,Schema,Data", "Comma-separated list of main menu entries in structr-ui. Everything not in this list will be moved into a sub-menu.");
//	public static final Setting<Boolean> MaintenanceModeActive  = new BooleanSetting(generalGroup,            "Application", "application.maintenance.activate", false, "Causes Structr to enter maintenance mode");
	public static final Setting<String> BasePath                = new StringSetting(generalGroup,             "Paths",       "base.path",                        ".", "Path of the Structr working directory. All files will be located relative to this directory.");
	public static final Setting<String> TmpPath                 = new StringSetting(generalGroup,             "Paths",       "tmp.path",                         System.getProperty("java.io.tmpdir"), "Path to the temporary directory. Uses <code>java.io.tmpdir</code> by default");
	public static final Setting<String> DatabasePath            = new StringSetting(generalGroup,             "Paths",       "database.path",                    "db", "Path of the Neo4j db/ directory");
	public static final Setting<String> FilesPath               = new StringSetting(generalGroup,             "Paths",       "files.path",                       System.getProperty("user.dir").concat(File.separator + "files"), "Path to the Structr file storage folder");
	public static final Setting<String> ChangelogPath           = new StringSetting(generalGroup,             "Paths",       "changelog.path",                   System.getProperty("user.dir").concat(File.separator + "changelog"), "Path to the Structr changelog storage folder");
	public static final Setting<String> DataExchangePath        = new StringSetting(generalGroup,             "Paths",       "data.exchange.path",               "exchange" + File.separator, "IMPORTANT: Path is relative to base.path");
	public static final Setting<String> SnapshotsPath           = new StringSetting(generalGroup,             "Paths",       "snapshot.path",                    "snapshots" + File.separator, "IMPORTANT: Path is relative to base.path");
	public static final Setting<String> WebDataPath             = new StringSetting(generalGroup,             "Paths",       "data.webapp.path",                 "webapp-data" + File.separator, "IMPORTANT: Path is relative to base.path");
	public static final Setting<Boolean> LogSchemaOutput        = new BooleanSetting(generalGroup,            "Logging",     "NodeExtender.log",                 false, "Whether to write dynamically created Java code to the logfile, for debugging purposes.");
	public static final Setting<Boolean> LogSchemaErrors        = new BooleanSetting(generalGroup,            "Logging",     "NodeExtender.log.errors",          true);
	public static final Setting<Boolean> RequestLogging         = new BooleanSetting(generalGroup,            "Logging",     "log.requests",                     false);
	public static final Setting<Boolean> DebugLogging           = new BooleanSetting(generalGroup,            "Logging",     "log.debug",                        false, "Controls the behaviour of the debug() function. If disabled, the debug() function behaves like a NOP. If enabled, it behaves exactly like the log() function.");
	public static final Setting<Boolean> LogFunctionsStackTrace = new BooleanSetting(generalGroup,            "Logging",     "log.functions.stacktrace",         false, "If true, the full stacktrace is logged for exceptions in built-in functions.");
	public static final Setting<String> LogPrefix               = new StringSetting(generalGroup,             "Logging",     "log.prefix",                       "structr");
	public static final Setting<Boolean> LogJSExcpetionRequest  = new BooleanSetting(generalGroup,            "Logging",     "log.javascript.exception.request", false, "Adds path, queryString and parameterMap to JavaScript exceptions (if available)");
	public static final Setting<Boolean> SetupWizardCompleted   = new BooleanSetting(generalGroup,            "hidden",      "setup.wizard.completed",           false);
	public static final Setting<String> Configuration           = new StringSetting(generalGroup,             "hidden",      "configuration.provider",           "org.structr.module.JarConfigurationProvider", "Fully-qualified class name of a Java class in the current class path that implements the <code>org.structr.schema.ConfigurationProvider</code> interface.");
	public static final StringMultiChoiceSetting Services       = new StringMultiChoiceSetting(generalGroup,  "Services",    "configured.services",              "NodeService SchemaService AgentService CronService HttpService", "Services that are listed in this configuration key will be started when Structr starts.");
	public static final Setting<Integer> ServicesStartTimeout   = new IntegerSetting(generalGroup,            "Services",    "services.start.timeout",           30);
	public static final Setting<Integer> ServicesStartRetries   = new IntegerSetting(generalGroup,            "Services",    "services.start.retries",           10);

	public static final Setting<Integer> NodeServiceStartTimeout = new IntegerSetting(generalGroup,  "Services",    "nodeservice.start.timeout",     30);
	public static final Setting<Integer> NodeServiceStartRetries = new IntegerSetting(generalGroup,  "Services",    "nodeservice.start.retries",     3);

	// server settings
	public static final Setting<String> ApplicationHost       = new StringSetting(serverGroup,  "Interfaces", "application.host",              "0.0.0.0", "The listen address of the Structr server");
	public static final Setting<Integer> HttpPort             = new IntegerSetting(serverGroup, "Interfaces", "application.http.port",         8082, "HTTP port the Structr server will listen on");
	public static final Setting<Integer> HttpsPort            = new IntegerSetting(serverGroup, "Interfaces", "application.https.port",        8083, "HTTPS port the Structr server will listen on (if SSL is enabled)");
	public static final Setting<Integer> SshPort              = new IntegerSetting(serverGroup, "Interfaces", "application.ssh.port",          8022, "SSH port the Structr server will listen on (if SSHService is enabled)");
	public static final Setting<Integer> FtpPort              = new IntegerSetting(serverGroup, "Interfaces", "application.ftp.port",          8021, "FTP port the Structr server will listen on (if FtpService is enabled)");
	public static final Setting<Boolean> HttpsEnabled         = new BooleanSetting(serverGroup, "Interfaces", "application.https.enabled",     false, "Whether SSL is enabled");
	public static final Setting<String> KeystorePath          = new StringSetting(serverGroup,  "Interfaces", "application.keystore.path",     "", "The path to the JKS keystore containing the SSL certificate");
	public static final Setting<String> KeystorePassword      = new StringSetting(serverGroup,  "Interfaces", "application.keystore.password", "", "The password for the JKS keystore");
	public static final Setting<String> RestPath              = new StringSetting(serverGroup,  "hidden",     "application.rest.path",         "/structr/rest", "Defines the URL path of the Structr REST server. Should not be changed because it is hard-coded in many parts of the application.");
	public static final Setting<String> BaseUrlOverride       = new StringSetting(serverGroup,  "Interfaces", "application.baseurl.override",  "", "Overrides the baseUrl that can be used to prefix links to local web resources. By default, the value is assembled from the protocol, hostname and port of the server instance Structr is running on");

	// HTTP service settings
	public static final Setting<String> ResourceHandlers         = new StringSetting(serverGroup,  "hidden",        "httpservice.resourcehandlers",         "StructrUiHandler", "This handler is needed to serve static files with the built-in Jetty container.");
	public static final Setting<String> LifecycleListeners       = new StringSetting(serverGroup,  "hidden",        "httpservice.lifecycle.listeners",      "");
	public static final Setting<Boolean> GzipCompression         = new BooleanSetting(serverGroup, "HTTP Settings", "httpservice.gzip.enabled",             true,  "Use GZIP compression for HTTP transfers");
	public static final Setting<Boolean> Async                   = new BooleanSetting(serverGroup, "HTTP Settings", "httpservice.async",                    true,  "Whether the HttpServices uses asynchronous request handling. Disable this option if you encounter problems with HTTP responses.");
	public static final Setting<Boolean> JsonIndentation         = new BooleanSetting(serverGroup, "HTTP Settings", "json.indentation",                     true,  "Whether JSON output should be indented (beautified) or compacted");
	public static final Setting<Boolean> HtmlIndentation         = new BooleanSetting(serverGroup, "HTTP Settings", "html.indentation",                     true,  "Whether the page source should be indented (beautified) or compacted. Note: Does not work for template/content nodes which contain raw HTML");
	public static final Setting<Boolean> WsIndentation           = new BooleanSetting(serverGroup, "HTTP Settings", "ws.indentation",                       false, "Prettyprints websocket responses if set to true.");
	public static final Setting<Integer> SessionTimeout          = new IntegerSetting(serverGroup, "HTTP Settings", "application.session.timeout",          1800,  "The session timeout for HTTP sessions. Unit is seconds. Default is 1800.");
	public static final Setting<Integer> MaxSessionsPerUser      = new IntegerSetting(serverGroup, "HTTP Settings", "application.session.max.number",       -1,    "The maximum number of active sessions per user. Default is -1 (unlimited).");
	public static final Setting<Boolean> ClearSessionsOnStartup  = new BooleanSetting(serverGroup, "HTTP Settings", "application.session.clear.onstartup",  false, "Clear all sessions on startup if set to true.");
	public static final Setting<Boolean> ClearSessionsOnShutdown = new BooleanSetting(serverGroup, "HTTP Settings", "application.session.clear.onshutdown", false, "Clear all sessions on shutdown if set to true.");

	public static final Setting<Boolean> ForceHttps             = new BooleanSetting(serverGroup, "HTTPS Settings", "httpservice.force.https",         		false, "Allows forcing HTTPS. (only works if HTTPS is active!)");
	public static final Setting<Boolean> HttpOnly               = new BooleanSetting(serverGroup, "HTTPS Settings", "httpservice.cookies.httponly",         	false, "Set HttpOnly to true for cookies. Please note that this will disable backend access!");
	public static final Setting<Boolean> dumpJettyStartupConfig = new BooleanSetting(serverGroup, "HTTPS Settings", "httpservice.log.jetty.startupconfig",  false);
	public static final Setting<String> excludedProtocols       = new StringSetting(serverGroup,  "HTTPS Settings", "httpservice.ssl.protocols.excluded",   "TLSv1,TLSv1.1");
	public static final Setting<String> includedProtocols       = new StringSetting(serverGroup,  "HTTPS Settings", "httpservice.ssl.protocols.included",   "TLSv1.2");
	public static final Setting<String> disabledCipherSuites    = new StringSetting(serverGroup,  "HTTPS Settings", "httpservice.ssl.ciphers.excluded",    	"");

	public static final Setting<String> AccessControlMaxAge           = new StringSetting(serverGroup, "CORS Settings", "access.control.max.age",           "3600", "Sets the value of the <code>Access-Control-Max-Age</code> header. Unit is seconds.");
	public static final Setting<String> AccessControlAllowMethods     = new StringSetting(serverGroup, "CORS Settings", "access.control.allow.methods",     "", "Sets the value of the <code>Access-Control-Allow-Methods</code> header. Comma-delimited list of the allowed HTTP request methods.");
	public static final Setting<String> AccessControlAllowHeaders     = new StringSetting(serverGroup, "CORS Settings", "access.control.allow.headers",     "", "Sets the value of the <code>Access-Control-Allow-Headers</code> header.");
	public static final Setting<String> AccessControlAllowCredentials = new StringSetting(serverGroup, "CORS Settings", "access.control.allow.credentials", "", "Sets the value of the <code>Access-Control-Allow-Credentials</code> header.");
	public static final Setting<String> AccessControlExposeHeaders    = new StringSetting(serverGroup, "CORS Settings", "access.control.expose.headers",    "", "Sets the value of the <code>Access-Control-Expose-Headers</code> header.");

	public static final Setting<String> UiHandlerContextPath        = new StringSetting(serverGroup,  "hidden", "structruihandler.contextpath",       "/structr", "Static resource handling configuration.");
	public static final Setting<Boolean> UiHandlerDirectoriesListed = new BooleanSetting(serverGroup, "hidden", "structruihandler.directorieslisted", false);
	public static final Setting<String> UiHandlerResourceBase       = new StringSetting(serverGroup,  "hidden", "structruihandler.resourcebase",      "src/main/resources/structr");
	public static final Setting<String> UiHandlerWelcomeFiles       = new StringSetting(serverGroup,  "hidden", "structruihandler.welcomefiles",      "index.html");

	// database settings
	public static final Setting<String> DatabaseAvailableConnections = new StringSetting(databaseGroup,  "hidden",                  "database.available.connections",   null);
	public static final Setting<String> DatabaseDriverMode           = new ChoiceSetting(databaseGroup,  "hidden",                  "database.driver.mode",             "embedded", Settings.getStringsAsSet("embedded", "remote"));
	public static final Setting<String> DatabaseDriver               = new StringSetting(databaseGroup,  "hidden",                  "database.driver",                  DEFAULT_DATABASE_DRIVER);
	public static final Setting<String> ConnectionName               = new StringSetting(databaseGroup,  "hidden",                  "database.connection.name",         "default");
	public static final Setting<String> SampleConnectionUrl          = new StringSetting(databaseGroup,  "hidden",                  "database.connection.url.sample",   "bolt://localhost:7687");
	public static final Setting<String> ConnectionUrl                = new StringSetting(databaseGroup,  "hidden",                  "database.connection.url",          "bolt://localhost:7688");
	public static final Setting<String> TestingConnectionUrl         = new StringSetting(databaseGroup,  "hidden",                  "testing.connection.url",           "bolt://localhost:7689");
	public static final Setting<String> ConnectionUser               = new StringSetting(databaseGroup,  "hidden",                  "database.connection.username",     "neo4j");
	public static final Setting<String> ConnectionPassword           = new StringSetting(databaseGroup,  "hidden",                  "database.connection.password",     "neo4j");
	public static final Setting<String> TenantIdentifier             = new StringSetting(databaseGroup,  "hidden",                  "database.tenant.identifier",       "");
	public static final Setting<Integer> RelationshipCacheSize       = new IntegerSetting(databaseGroup, "Caching",                 "database.cache.relationship.size", 500000);
	public static final Setting<Integer> NodeCacheSize               = new IntegerSetting(databaseGroup, "Caching",                 "database.cache.node.size",         100000, "Size of the database driver node cache");
	public static final Setting<Integer> UuidCacheSize               = new IntegerSetting(databaseGroup, "hidden",                  "database.cache.uuid.size",         1000000, "Size of the database driver relationship cache");
	public static final Setting<Boolean> ForceResultStreaming        = new BooleanSetting(databaseGroup, "Result Streaming",        "database.result.lazy",             false, "Forces Structr to use lazy evaluation for relationship queries");
	public static final Setting<Boolean> CypherDebugLogging          = new BooleanSetting(databaseGroup, "Debugging",               "log.cypher.debug",                 false, "Turns on debug logging for the generated Cypher queries");
	public static final Setting<Boolean> CypherDebugLoggingPing      = new BooleanSetting(databaseGroup, "Debugging",               "log.cypher.debug.ping",            false, "Turns on debug logging for the generated Cypher queries of the websocket PING command. Can only be used in conjunction with log.cypher.debug");
	public static final Setting<Boolean> SyncDebugging               = new BooleanSetting(databaseGroup, "Sync debugging",          "sync.debug",                       false);
	public static final Setting<Integer> ResultCountSoftLimit        = new IntegerSetting(databaseGroup, "Soft result count limit", "database.result.softlimit",        10_000, "Soft result count limit for a single query (can be overridden by pageSize)");
	public static final Setting<Integer> FetchSize                   = new IntegerSetting(databaseGroup, "Result fetch size",       "database.result.fetchsize",        100_000, "Number of database records to fetch per batch when fetching large results");

	// application settings
	public static final Setting<Boolean> ChangelogEnabled         = new BooleanSetting(applicationGroup, "Changelog",    "application.changelog.enabled",               false, "Turns on logging of changes to nodes and relationships");
	public static final Setting<Boolean> UserChangelogEnabled     = new BooleanSetting(applicationGroup, "Changelog",    "application.changelog.user_centric.enabled",  false, "Turns on user-centric logging of what a user changed/created/deleted");
	public static final Setting<Boolean> FilesystemEnabled        = new BooleanSetting(applicationGroup, "Filesystem",   "application.filesystem.enabled",              false, "If enabled, Structr will create a separate home directory for each user. See Filesystem for more information.");
	public static final Setting<Boolean> UniquePaths              = new BooleanSetting(applicationGroup, "Filesystem",   "application.filesystem.unique.paths",         true,  "If enabled, Structr will not allow files of the same name in the same folder.");
	public static final Setting<String> DefaultChecksums          = new StringSetting(applicationGroup,  "Filesystem",   "application.filesystem.checksums.default",    "",    "List of checksums to be calculated on file creation by default.");
	public static final Setting<Boolean> IndexingEnabled          = new BooleanSetting(applicationGroup, "Filesystem",   "application.filesystem.indexing.enabled",     true,  "Whether indexing is enabled globally (can be controlled separately for each file)");
	public static final Setting<Integer> IndexingMaxFileSize      = new IntegerSetting(applicationGroup, "Filesystem",   "application.filesystem.indexing.maxsize",     10,    "Maximum size (MB) of a file to be indexed");
	public static final Setting<Integer> IndexingLimit            = new IntegerSetting(applicationGroup, "Filesystem",   "application.filesystem.indexing.limit",       50000, "Maximum number of words to be indexed per file.");
	public static final Setting<Integer> IndexingMinLength        = new IntegerSetting(applicationGroup, "Filesystem",   "application.filesystem.indexing.minlength",   3,     "Minimum length of words to be indexed");
	public static final Setting<Integer> IndexingMaxLength        = new IntegerSetting(applicationGroup, "Filesystem",   "application.filesystem.indexing.maxlength",   30,    "Maximum length of words to be indexed");
	public static final Setting<Boolean> FollowSymlinks           = new BooleanSetting(applicationGroup, "Filesystem",   "application.filesystem.mount.followsymlinks", true);
	public static final Setting<String> DefaultUploadFolder       = new StringSetting(applicationGroup,  "Filesystem",   "application.uploads.folder",                  "", "The default path for files uploaded via the UploadServlet (available from Structr 2.1+)");

	public static final Setting<String> HttpProxyUrl              = new StringSetting(applicationGroup,  "Proxy",        "application.proxy.http.url",                  "");
	public static final Setting<String> HttpProxyUser             = new StringSetting(applicationGroup,  "Proxy",        "application.proxy.http.username",             "");
	public static final Setting<String> HttpProxyPassword         = new StringSetting(applicationGroup,  "Proxy",        "application.proxy.http.password",             "");

	public static final Setting<Integer> HttpConnectionRequestTimeout = new IntegerSetting(applicationGroup, "Outgoing Connection Timeouts",   "application.httphelper.timeouts.connectionrequest",   60,    "Applies when making outgoing connections. Returns the timeout in <b>seconds</b> used when requesting a connection from the connection manager. A timeout value of zero is interpreted as an infinite timeout.");
	public static final Setting<Integer> HttpConnectTimeout           = new IntegerSetting(applicationGroup, "Outgoing Connection Timeouts",   "application.httphelper.timeouts.connect",             60,    "Applies when making outgoing connections. Determines the timeout in <b>seconds</b> until a connection is established. A timeout value of zero is interpreted as an infinite timeout.");
	public static final Setting<Integer> HttpSocketTimeout            = new IntegerSetting(applicationGroup, "Outgoing Connection Timeouts",   "application.httphelper.timeouts.socket",             600,    "Applies when making outgoing connections. Defines the socket timeout in <b>seconds</b>, which is the timeout for waiting for data or, put differently, a maximum period inactivity between two consecutive data packets. A timeout value of zero is interpreted as an infinite timeout.");

	public static final Setting<Boolean> SchemaAutoMigration      = new BooleanSetting(applicationGroup, "Schema",       "application.schema.automigration",            false, "Enable automatic migration of schema information between versions (if possible -- may delete schema nodes)");
	public static final Setting<Boolean> AllowUnknownPropertyKeys = new BooleanSetting(applicationGroup, "Schema",       "application.schema.allowunknownkeys",         false, "Enables get() and set() built-in functions to use property keys that are not defined in the schema.");
	public static final Setting<Boolean> logMissingLocalizations  = new BooleanSetting(applicationGroup, "Localization", "application.localization.logmissing",         false, "Turns on logging for requested but non-existing localizations.");
	public static final Setting<String> SchemaDeploymentFormat    = new ChoiceSetting(applicationGroup,  "Deployment",   "deployment.schema.format",                    "tree", Settings.getStringsAsSet("file", "tree"), "Configures how the schema is exported in a deployment export. <code>file</code> exports the schema as a single file. <code>tree</code> exports the schema as a tree where methods/function properties are written to single files in a tree structure.");
	public static final Setting<String> GlobalSecret              = new StringSetting(applicationGroup,  "Encryption",   "application.encryption.secret",               null,   "Sets the global secret for encrypted string properties. Using this configuration setting is one of several possible ways to set the secret, and it is not recommended for production environments because the key can easily be read by an attacker with scripting access.");

	public static final Setting<Boolean> CallbacksOnLogout      = new BooleanSetting(applicationGroup, "Login/Logout behavior",   "callbacks.logout.onsave",       false, "Setting this to true enables the execution of the User.onSave method when a user logs out. Disabled by default because the global login handler onStructrLogout would be the right place for such functionality.");
	public static final Setting<Boolean> CallbacksOnLogin       = new BooleanSetting(applicationGroup, "Login/Logout behavior",   "callbacks.login.onsave",      false, "Setting this to true enables the execution of the User.onSave method for login actions. This will also trigger for failed login attempts and for two-factor authentication intermediate steps. Disabled by default because the global login handler onStructrLogin would be the right place for such functionality.");


	// mail settings
	public static final Setting<String> SmtpHost              = new StringSetting(smtpGroup,  "SMTP Settings", "smtp.host",         "localhost", "Address of the SMTP server used to send e-mails");
	public static final Setting<Integer> SmtpPort             = new IntegerSetting(smtpGroup, "SMTP Settings", "smtp.port",         25,          "SMTP server port to use when sending e-mails");
	public static final Setting<String> SmtpUser              = new StringSetting(smtpGroup,  "SMTP Settings", "smtp.user",         "");
	public static final Setting<String> SmtpPassword          = new StringSetting(smtpGroup,  "SMTP Settings", "smtp.password",     "");
	public static final Setting<Boolean> SmtpTlsEnabled       = new BooleanSetting(smtpGroup, "SMTP Settings", "smtp.tls.enabled",  true,        "Whether to use TLS when sending e-mails");
	public static final Setting<Boolean> SmtpTlsRequired      = new BooleanSetting(smtpGroup, "SMTP Settings", "smtp.tls.required", true,        "Whether TLS is required when sending e-mails");
	public static final Setting<Boolean> SmtpTesting          = new BooleanSetting(smtpGroup, "hidden",        "smtp.testing.only", false);

	// advanced settings
	public static final Setting<Boolean> JsonRedundancyReduction      = new BooleanSetting(advancedGroup, "JSON",   "json.redundancyreduction",       true);
	public static final Setting<Integer> JsonParallelizationThreshold = new IntegerSetting(advancedGroup, "JSON",   "json.parallelization.threshold", 100, "Collection size threshold for multi-threaded JSON generation");
	public static final Setting<Boolean> JsonLenient                  = new BooleanSetting(advancedGroup, "JSON",   "json.lenient",                   false, "Whether to use lenient serialization, e.g. allow to serialize NaN, -Infinity, Infinity instead of just returning null. Note: as long as Javascript doesn’t support NaN etc., most of the UI will be broken");
	public static final Setting<Boolean> ForceArrays                  = new BooleanSetting(advancedGroup, "JSON",   "json.output.forcearrays",        false);

	public static final Setting<String> GeocodingProvider        = new StringSetting(advancedGroup,  "Geocoding",   "geocoding.provider",            "org.structr.common.geo.GoogleGeoCodingProvider", "Geocoding configuration");
	public static final Setting<String> GeocodingLanguage        = new StringSetting(advancedGroup,  "Geocoding",   "geocoding.language",            "de", "Geocoding configuration");
	public static final Setting<String> GeocodingApiKey          = new StringSetting(advancedGroup,  "Geocoding",   "geocoding.apikey",              "", "Geocoding configuration");
	public static final Setting<String> DefaultDateFormat        = new StringSetting(advancedGroup,  "Date Format", "dateproperty.defaultformat",    "yyyy-MM-dd'T'HH:mm:ssZ", "Default ISO8601 date format pattern");
	public static final Setting<Boolean> InheritanceDetection    = new BooleanSetting(advancedGroup, "hidden",      "importer.inheritancedetection", true);
	public static final Setting<Boolean> CmisEnabled             = new BooleanSetting(advancedGroup, "hidden",      "cmis.enabled",                  false);

	// servlets
	public static final StringMultiChoiceSetting Servlets     = new StringMultiChoiceSetting(servletsGroup, "General", "httpservice.servlets", "JsonRestServlet HtmlServlet WebSocketServlet CsvServlet UploadServlet ProxyServlet GraphQLServlet DeploymentServlet LoginServlet LogoutServlet", Settings.getStringsAsSet("JsonRestServlet", "HtmlServlet", "WebSocketServlet", "CsvServlet", "UploadServlet", "ProxyServlet", "GraphQLServlet", "DeploymentServlet", "FlowServlet", "LoginServlet", "LogoutServlet"), "Servlets that are listed in this configuration key will be available in the HttpService. Changes to this setting require a restart of the HttpService in the 'Services' tab.");

	public static final Setting<Boolean> ConfigServletEnabled = new BooleanSetting(servletsGroup,  "ConfigServlet", "configservlet.enabled",             true, "Enables the config servlet (available under <code>http(s)://<your-server>/structr/config</code>)");

	public static final Setting<String> RestServletPath       = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.path",                         "/structr/rest/*", "URL pattern for REST server. Do not change unless you know what you are doing.");
	public static final Setting<String> RestServletClass      = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.class",                        "org.structr.rest.servlet.JsonRestServlet", "FQCN of servlet class to use in the REST server. Do not change unless you know what you are doing.");
	public static final Setting<String> RestAuthenticator     = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.authenticator",                "org.structr.web.auth.UiAuthenticator", "FQCN of authenticator class to use in the REST server. Do not change unless you know what you are doing.");
	public static final Setting<String> RestDefaultView       = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.defaultview",                  "public", "Default view to use when no view is given in the URL");
	public static final Setting<Integer> RestOutputDepth      = new IntegerSetting(servletsGroup,           "JsonRestServlet", "jsonrestservlet.outputdepth",                  3, "Maximum nesting depth of JSON output");
	public static final Setting<String> RestResourceProvider  = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.resourceprovider",             "org.structr.web.common.UiResourceProvider", "FQCN of resource provider class to use in the REST server. Do not change unless you know what you are doing.");
	public static final Setting<String> RestUserClass         = new StringSetting(servletsGroup,            "JsonRestServlet", "jsonrestservlet.user.class",                   "org.structr.dynamic.User");
	public static final Setting<Boolean> RestUserAutologin    = new BooleanSetting(servletsGroup,           "JsonRestServlet", "jsonrestservlet.user.autologin",               false);
	public static final Setting<Boolean> RestUserAutocreate   = new BooleanSetting(servletsGroup,           "JsonRestServlet", "jsonrestservlet.user.autocreate",              false, "Enable this to support user self registration");
	public static final Setting<String> InputValidationMode   = new StringMultiChoiceSetting(servletsGroup, "JsonRestServlet", "jsonrestservlet.unknowninput.validation.mode", "ignore", new LinkedHashSet<>(Arrays.asList("accept", "warn", "ignore", "reject")), "Controls how Structr reacts to unknown keys in JSON input.");

	public static final Setting<String> FlowServletPath       = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.path",             "/structr/flow/*");
	public static final Setting<String> FlowServletClass      = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.class",            "org.structr.flow.servlet.FlowServlet");
	public static final Setting<String> FlowAuthenticator     = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.authenticator",    "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> FlowDefaultView       = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.defaultview",      "public");
	public static final Setting<Integer> FlowOutputDepth      = new IntegerSetting(servletsGroup, "FlowServlet", "flowservlet.outputdepth",      3);
	public static final Setting<String> FlowResourceProvider  = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.resourceprovider", "org.structr.web.common.UiResourceProvider");
	public static final Setting<String> FlowUserClass         = new StringSetting(servletsGroup,  "FlowServlet", "flowservlet.user.class",       "org.structr.dynamic.User");
	public static final Setting<Boolean> FlowUserAutologin    = new BooleanSetting(servletsGroup, "FlowServlet", "flowservlet.user.autologin",   false);
	public static final Setting<Boolean> FlowUserAutocreate   = new BooleanSetting(servletsGroup, "FlowServlet", "flowservlet.user.autocreate",  false);

	public static final Setting<String> HtmlServletPath           = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.path",                  "/structr/html/*", "URL pattern for HTTP server. Do not change unless you know what you are doing.");
	public static final Setting<String> HtmlServletClass          = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.class",                 "org.structr.web.servlet.HtmlServlet", "FQCN of servlet class to use for HTTP requests. Do not change unless you know what you are doing.");
	public static final Setting<String> HtmlAuthenticator         = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.authenticator",         "org.structr.web.auth.UiAuthenticator", "FQCN of authenticator class to use for HTTP requests. Do not change unless you know what you are doing.");
	public static final Setting<String> HtmlDefaultView           = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.defaultview",           "public", "Not used for HtmlServlet");
	public static final Setting<Integer> HtmlOutputDepth          = new IntegerSetting(servletsGroup, "HtmlServlet", "htmlservlet.outputdepth",           3, "Not used for HtmlServlet");
	public static final Setting<String> HtmlResourceProvider      = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider", "FQCN of resource provider class to use in the HTTP server. Do not change unless you know what you are doing.");
	public static final Setting<Boolean> HtmlUserAutologin        = new BooleanSetting(servletsGroup, "HtmlServlet", "htmlservlet.user.autologin",        false);
	public static final Setting<Boolean> HtmlUserAutocreate       = new BooleanSetting(servletsGroup, "HtmlServlet", "htmlservlet.user.autocreate",       true);
	public static final Setting<String> HtmlResolveProperties     = new StringSetting(servletsGroup,  "HtmlServlet", "htmlservlet.resolveproperties",     "AbstractNode.name", "Specifies the list of properties that are be used to resolve entities from URL paths.");
	public static final Setting<String> HtmlCustomResponseHeaders = new TextSetting(servletsGroup,    "HtmlServlet", "htmlservlet.customresponseheaders", "Strict-Transport-Security:max-age=60,X-Content-Type-Options:nosniff,X-Frame-Options:SAMEORIGIN,X-XSS-Protection:1;mode=block", "List of custom response headers that will be added to every HTTP response");

	public static final Setting<String> PdfServletPath           = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.path",                  "/structr/pdf/*");
	public static final Setting<String> PdfServletClass          = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.class",                 "org.structr.pdf.servlet.PdfServlet");
	public static final Setting<String> PdfAuthenticator         = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.authenticator",         "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> PdfDefaultView           = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.defaultview",           "public");
	public static final Setting<Integer> PdfOutputDepth          = new IntegerSetting(servletsGroup, "PdfServlet", "pdfservlet.outputdepth",           3);
	public static final Setting<String> PdfResourceProvider      = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider");
	public static final Setting<Boolean> PdfUserAutologin        = new BooleanSetting(servletsGroup, "PdfServlet", "pdfservlet.user.autologin",        false);
	public static final Setting<Boolean> PdfUserAutocreate       = new BooleanSetting(servletsGroup, "PdfServlet", "pdfservlet.user.autocreate",       true);
	public static final Setting<String> PdfResolveProperties     = new StringSetting(servletsGroup,  "PdfServlet", "pdfservlet.resolveproperties",     "AbstractNode.name");
	public static final Setting<String> PdfCustomResponseHeaders = new TextSetting(servletsGroup,    "PdfServlet", "pdfservlet.customresponseheaders", "Strict-Transport-Security:max-age=60,X-Content-Type-Options:nosniff,X-Frame-Options:SAMEORIGIN,X-XSS-Protection:1;mode=block");

	public static final Setting<String> WebsocketServletPath       = new StringSetting(servletsGroup,  "WebSocketServlet", "websocketservlet.path",              "/structr/ws/*", "URL pattern for WebSockets. Do not change unless you know what you are doing.");
	public static final Setting<String> WebsocketServletClass      = new StringSetting(servletsGroup,  "WebSocketServlet", "websocketservlet.class",             "org.structr.websocket.servlet.WebSocketServlet", "FQCN of servlet class to use for WebSockets. Do not change unless you know what you are doing.");
	public static final Setting<String> WebsocketAuthenticator     = new StringSetting(servletsGroup,  "WebSocketServlet", "websocketservlet.authenticator",     "org.structr.web.auth.UiAuthenticator", "FQCN of authenticator class to use for WebSockets. Do not change unless you know what you are doing.");
	public static final Setting<String> WebsocketDefaultView       = new StringSetting(servletsGroup,  "WebSocketServlet", "websocketservlet.defaultview",       "public", "Unused");
	public static final Setting<Integer> WebsocketOutputDepth      = new IntegerSetting(servletsGroup, "WebSocketServlet", "websocketservlet.outputdepth",       3, "Maximum nesting depth of JSON output");
	public static final Setting<String> WebsocketResourceProvider  = new StringSetting(servletsGroup,  "WebSocketServlet", "websocketservlet.resourceprovider",  "org.structr.web.common.UiResourceProvider", "FQCN of resource provider class to use with WebSockets. Do not change unless you know what you are doing.");
	public static final Setting<Boolean> WebsocketUserAutologin    = new BooleanSetting(servletsGroup, "WebSocketServlet", "websocketservlet.user.autologin",    false, "Unused");
	public static final Setting<Boolean> WebsocketUserAutocreate   = new BooleanSetting(servletsGroup, "WebSocketServlet", "websocketservlet.user.autocreate",   false, "Unused");
	public static final Setting<Boolean> WebsocketFrontendAccess   = new BooleanSetting(servletsGroup, "WebSocketServlet", "websocketservlet.frontendaccess",    false);

	public static final Setting<String> CsvServletPath       = new StringSetting(servletsGroup,  "CsvServlet", "csvservlet.path",              "/structr/csv/*", "URL pattern for CSV output. Do not change unless you know what you are doing.");
	public static final Setting<String> CsvServletClass      = new StringSetting(servletsGroup,  "CsvServlet", "csvservlet.class",             "org.structr.rest.servlet.CsvServlet", "Servlet class to use for CSV output. Do not change unless you know what you are doing.");
	public static final Setting<String> CsvAuthenticator     = new StringSetting(servletsGroup,  "CsvServlet", "csvservlet.authenticator",     "org.structr.web.auth.UiAuthenticator", "FQCN of Authenticator class to use for CSV output. Do not change unless you know what you are doing.");
	public static final Setting<String> CsvDefaultView       = new StringSetting(servletsGroup,  "CsvServlet", "csvservlet.defaultview",       "public", "Default view to use when no view is given in the URL");
	public static final Setting<Integer> CsvOutputDepth      = new IntegerSetting(servletsGroup, "CsvServlet", "csvservlet.outputdepth",       3, "Maximum nesting depth of JSON output");
	public static final Setting<String> CsvResourceProvider  = new StringSetting(servletsGroup,  "CsvServlet", "csvservlet.resourceprovider",  "org.structr.web.common.UiResourceProvider", "FQCN of resource provider class to use in the REST server. Do not change unless you know what you are doing.");
	public static final Setting<Boolean> CsvUserAutologin    = new BooleanSetting(servletsGroup, "CsvServlet", "csvservlet.user.autologin",    false, "Unused");
	public static final Setting<Boolean> CsvUserAutocreate   = new BooleanSetting(servletsGroup, "CsvServlet", "csvservlet.user.autocreate",   false, "Unused");
	public static final Setting<Boolean> CsvFrontendAccess   = new BooleanSetting(servletsGroup, "CsvServlet", "csvservlet.frontendaccess",    false, "Unused");

	public static final Setting<String> UploadServletPath       = new StringSetting(servletsGroup,  "UploadServlet", "uploadservlet.path",                  "/structr/upload", "URL pattern for file upload. Do not change unless you know what you are doing.");
	public static final Setting<String> UploadServletClass      = new StringSetting(servletsGroup,  "UploadServlet", "uploadservlet.class",                 "org.structr.web.servlet.UploadServlet", "FQCN of servlet class to use for file upload. Do not change unless you know what you are doing.");
	public static final Setting<String> UploadAuthenticator     = new StringSetting(servletsGroup,  "UploadServlet", "uploadservlet.authenticator",         "org.structr.web.auth.UiAuthenticator", "FQCN of authenticator class to use for file upload. Do not change unless you know what you are doing.");
	public static final Setting<String> UploadDefaultView       = new StringSetting(servletsGroup,  "UploadServlet", "uploadservlet.defaultview",           "public", "Default view to use when no view is given in the URL");
	public static final Setting<Integer> UploadOutputDepth      = new IntegerSetting(servletsGroup, "UploadServlet", "uploadservlet.outputdepth",           3, "Maximum nesting depth of JSON output");
	public static final Setting<String> UploadResourceProvider  = new StringSetting(servletsGroup,  "UploadServlet", "uploadservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider", "FQCN of resource provider class to use for file upload. Do not change unless you know what you are doing.	");
	public static final Setting<Boolean> UploadUserAutologin    = new BooleanSetting(servletsGroup, "UploadServlet", "uploadservlet.user.autologin",        false);
	public static final Setting<Boolean> UploadUserAutocreate   = new BooleanSetting(servletsGroup, "UploadServlet", "uploadservlet.user.autocreate",       false, "Unused");
	public static final Setting<Boolean> UploadAllowAnonymous   = new BooleanSetting(servletsGroup, "UploadServlet", "uploadservlet.allowanonymousuploads", false);
	public static final Setting<Integer> UploadMaxFileSize      = new IntegerSetting(servletsGroup, "UploadServlet", "uploadservlet.maxfilesize",           1000, "Maximum allowed file size for single file uploads. Unit is Megabytes");
	public static final Setting<Integer> UploadMaxRequestSize   = new IntegerSetting(servletsGroup, "UploadServlet", "uploadservlet.maxrequestsize",        1200, "Maximum allowed request size for single file uploads. Unit is Megabytes");

	public static final Setting<String> GraphQLServletPath       = new StringSetting(servletsGroup,  "GraphQLServlet", "graphqlservlet.path",                  "/structr/graphql");
	public static final Setting<String> GraphQLServletClass      = new StringSetting(servletsGroup,  "GraphQLServlet", "graphqlservlet.class",                 "org.structr.rest.servlet.GraphQLServlet");
	public static final Setting<String> GraphQLAuthenticator     = new StringSetting(servletsGroup,  "GraphQLServlet", "graphqlservlet.authenticator",         "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> GraphQLResourceProvider  = new StringSetting(servletsGroup,  "GraphQLServlet", "graphqlservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider");
	public static final Setting<String> GraphQLDefaultView       = new StringSetting(servletsGroup,  "GraphQLServlet", "graphqlservlet.defaultview",           "public");
	public static final Setting<Integer> GraphQLOutputDepth      = new IntegerSetting(servletsGroup, "GraphQLServlet", "graphqlservlet.outputdepth",	   3);

	public static final Setting<String> LoginServletPath       = new StringSetting(servletsGroup,  "LoginServlet", "loginservlet.path",                  "/structr/login");
	public static final Setting<String> LoginServletClass      = new StringSetting(servletsGroup,  "LoginServlet", "loginservlet.class",                 "org.structr.web.servlet.LoginServlet");
	public static final Setting<String> LoginAuthenticator     = new StringSetting(servletsGroup,  "LoginServlet", "loginservlet.authenticator",         "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> LoginResourceProvider  = new StringSetting(servletsGroup,  "LoginServlet", "loginservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider");
	public static final Setting<String> LoginDefaultView       = new StringSetting(servletsGroup,  "LoginServlet", "loginservlet.defaultview",           "public");
	public static final Setting<Integer> LoginOutputDepth      = new IntegerSetting(servletsGroup, "LoginServlet", "loginservlet.outputdepth",	   3);

	public static final Setting<String> LogoutServletPath       = new StringSetting(servletsGroup,  "LogoutServlet", "logoutservlet.path",                  "/structr/logout");
	public static final Setting<String> LogoutServletClass      = new StringSetting(servletsGroup,  "LogoutServlet", "logoutservlet.class",                 "org.structr.web.servlet.LogoutServlet");
	public static final Setting<String> LogoutAuthenticator     = new StringSetting(servletsGroup,  "LogoutServlet", "logoutservlet.authenticator",         "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> LogoutResourceProvider  = new StringSetting(servletsGroup,  "LogoutServlet", "logoutservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider");
	public static final Setting<String> LogoutDefaultView       = new StringSetting(servletsGroup,  "LogoutServlet", "logoutservlet.defaultview",           "public");
	public static final Setting<Integer> LogoutOutputDepth      = new IntegerSetting(servletsGroup, "LogoutServlet", "logoutservlet.outputdepth",	   3);

	public static final Setting<String> DeploymentServletPath                = new StringSetting(servletsGroup,  "DeploymentServlet", "deploymentservlet.path",                      "/structr/deploy");
	public static final Setting<String> DeploymentServletClass               = new StringSetting(servletsGroup,  "DeploymentServlet", "deploymentservlet.class",                     "org.structr.web.servlet.DeploymentServlet");
	public static final Setting<String> DeploymentAuthenticator              = new StringSetting(servletsGroup,  "DeploymentServlet", "deploymentservlet.authenticator",             "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> DeploymentDefaultView                = new StringSetting(servletsGroup,  "DeploymentServlet", "deploymentservlet.defaultview",               "public");
	public static final Setting<Integer> DeploymentOutputDepth               = new IntegerSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.outputdepth",               3);
	public static final Setting<String> DeploymentResourceProvider           = new StringSetting(servletsGroup,  "DeploymentServlet", "deploymentservlet.resourceprovider",          "org.structr.web.common.UiResourceProvider");
	public static final Setting<Boolean> DeploymentUserAutologin             = new BooleanSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.user.autologin",            false);
	public static final Setting<Boolean> DeploymentUserAutocreate            = new BooleanSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.user.autocreate",           false);
	public static final Setting<Boolean> DeploymentAllowAnonymousDeployments = new BooleanSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.allowanonymousdeployments", false);
	public static final Setting<Boolean> DeploymentAllowAnonymousUploads     = new BooleanSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.allowanonymousuploads",     false);
	public static final Setting<Integer> DeploymentMaxFileSize               = new IntegerSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.maxfilesize",               1000);
	public static final Setting<Integer> DeploymentMaxRequestSize            = new IntegerSetting(servletsGroup, "DeploymentServlet", "deploymentservlet.maxrequestsize",            1200);

	public static final Setting<String> ProxyServletPath       = new StringSetting(servletsGroup,  "ProxyServlet", "proxyservlet.path",                  "/structr/proxy");
	public static final Setting<String> ProxyServletClass      = new StringSetting(servletsGroup,  "ProxyServlet", "proxyservlet.class",                 "org.structr.web.servlet.ProxyServlet");
	public static final Setting<String> ProxyAuthenticator     = new StringSetting(servletsGroup,  "ProxyServlet", "proxyservlet.authenticator",         "org.structr.web.auth.UiAuthenticator");
	public static final Setting<String> ProxyDefaultView       = new StringSetting(servletsGroup,  "ProxyServlet", "proxyservlet.defaultview",           "public");
	public static final Setting<Integer> ProxyOutputDepth      = new IntegerSetting(servletsGroup, "ProxyServlet", "proxyservlet.outputdepth",           3);
	public static final Setting<String> ProxyResourceProvider  = new StringSetting(servletsGroup,  "ProxyServlet", "proxyservlet.resourceprovider",      "org.structr.web.common.UiResourceProvider");
	public static final Setting<Boolean> ProxyUserAutologin    = new BooleanSetting(servletsGroup, "ProxyServlet", "proxyservlet.user.autologin",        false);
	public static final Setting<Boolean> ProxyUserAutocreate   = new BooleanSetting(servletsGroup, "ProxyServlet", "proxyservlet.user.autocreate",       false);
	public static final Setting<Boolean> ProxyAllowAnonymous   = new BooleanSetting(servletsGroup, "ProxyServlet", "proxyservlet.allowanonymousproxys", false);
	public static final Setting<Integer> ProxyMaxFileSize      = new IntegerSetting(servletsGroup, "ProxyServlet", "proxyservlet.maxfilesize",           1000);
	public static final Setting<Integer> ProxyMaxRequestSize   = new IntegerSetting(servletsGroup, "ProxyServlet", "proxyservlet.maxrequestsize",        1200);

	// cron settings
	public static final Setting<String> CronTasks                   = new StringSetting(cronGroup,  "", "CronService.tasks", "", "List with cron task configurations");
	public static final Setting<Boolean> CronAllowParallelExecution = new BooleanSetting(cronGroup,  "", "CronService.allowparallelexecution", false, "Enables the parallel execution of *the same* cron job. This can happen if the method runs longer than the defined cron interval. As thisand could possibly create problems the default is false.");

	//security settings
	public static final Setting<String> SuperUserName                  = new StringSetting(securityGroup,     "Superuser",            "superuser.username",                    "superadmin", "Name of the superuser");
	public static final Setting<String> SuperUserPassword              = new PasswordSetting(securityGroup,   "Superuser",            "superuser.password",                    null, "Password of the superuser");
	public static final Setting<Integer> ResolutionDepth               = new IntegerSetting(applicationGroup, "Application Security", "application.security.resolution.depth", 5);
	public static final Setting<String> OwnerlessNodes                 = new StringSetting(applicationGroup,  "Application Security", "application.security.ownerless.nodes",  "read", "The permission level for users on nodes without an owner. One or more of: <code>read, write, delete, accessControl</code>");
	public static final Setting<Boolean> XMLParserSecurity             = new BooleanSetting(applicationGroup, "Application Security", "application.xml.parser.security", true, "Enables various security measures for XML parsing to prevent exploits.");

	public static final Setting<Integer> TwoFactorLevel                = new IntegerChoiceSetting(securityGroup, "Two Factor Authentication", "security.twofactorauthentication.level",                1,             Settings.getTwoFactorSettingOptions());
	public static final Setting<String> TwoFactorIssuer                = new StringSetting(securityGroup,        "Two Factor Authentication", "security.twofactorauthentication.issuer",               "Structr",     "Must be URL-compliant in order to scan the created QR code");
	public static final Setting<String> TwoFactorAlgorithm             = new ChoiceSetting(securityGroup,        "Two Factor Authentication", "security.twofactorauthentication.algorithm",            "SHA1",        Settings.getStringsAsSet("SHA1", "SHA256", "SHA512"), "Respected by the most recent Google Authenticator implementations. <i>Warning: Changing this setting after users are already confirmed will effectively lock them out. Set [User].twoFactorConfirmed to false to show them a new QR code.</i>");
	public static final Setting<Integer> TwoFactorDigits               = new IntegerChoiceSetting(securityGroup, "Two Factor Authentication", "security.twofactorauthentication.digits",               6,             Settings.getTwoFactorDigitsOptions(), "Respected by the most recent Google Authenticator implementations. <i>Warning: Changing this setting after users are already confirmed may lock them out. Set [User].twoFactorConfirmed to false to show them a new QR code.</i>");
	public static final Setting<Integer> TwoFactorPeriod               = new IntegerSetting(securityGroup,       "Two Factor Authentication", "security.twofactorauthentication.period",               30,            "Defines the period that a TOTP code will be valid for, in seconds.<br>Respected by the most recent Google Authenticator implementations. <i>Warning: Changing this setting after users are already confirmed will effectively lock them out. Set [User].twoFactorConfirmed to false to show them a new QR code.</i>");
	public static final Setting<Integer> TwoFactorLoginTimeout         = new IntegerSetting(securityGroup,       "Two Factor Authentication", "security.twofactorauthentication.logintimeout",         30,            "Defines how long the two-factor login time window in seconds is. After entering the username and password the user has this amount of time to enter a two factor token before he has to re-authenticate via password");
	public static final Setting<String> TwoFactorLoginPage             = new StringSetting(securityGroup,        "Two Factor Authentication", "security.twofactorauthentication.loginpage",            "/twofactor",  "The application page where the user enters the current two factor token");
	public static final Setting<String> TwoFactorWhitelistedIPs        = new StringSetting(securityGroup,        "Two Factor Authentication", "security.twofactorauthentication.whitelistedips",       "",            "A comma-separated (,) list of IPs for which two factor authentication is disabled.");

	public static final Setting<Boolean> PasswordForceChange                 = new BooleanSetting(securityGroup, "Password Policy", "security.passwordpolicy.forcechange",                         false, "Indicates if a forced password change is active");
	public static final Setting<Boolean> PasswordClearSessionsOnChange       = new BooleanSetting(securityGroup, "Password Policy", "security.passwordpolicy.onchange.clearsessions",              false, "Clear all sessions of a user on password change.");
	public static final Setting<Integer> PasswordForceChangeDays             = new IntegerSetting(securityGroup, "Password Policy", "security.passwordpolicy.maxage",                              90,    "The number of days after which a user has to change his password");
	public static final Setting<Integer> PasswordForceChangeReminder         = new IntegerSetting(securityGroup, "Password Policy", "security.passwordpolicy.remindtime",                          14,    "The number of days (before the user must change the password) where a warning should be issued. (Has to be handled in application code)");
	public static final Setting<Integer> PasswordAttempts                    = new IntegerSetting(securityGroup, "Password Policy", "security.passwordpolicy.maxfailedattempts",                   4,     "The maximum number of failed login attempts before a user is blocked. (Can be disabled by setting to zero or a negative number)");
	public static final Setting<Boolean> PasswordResetFailedCounterOnPWReset = new BooleanSetting(securityGroup, "Password Policy", "security.passwordpolicy.resetFailedAttemptsOnPasswordReset",  true,  "Configures if resetting the users password also resets the failed login attempts counter");

	public static final Setting<String> RegistrationCustomUserClass               = new StringSetting(securityGroup,  "User Self Registration", "registration.customuserclass",              "");
	public static final Setting<Boolean> RegistrationAllowLoginBeforeConfirmation = new BooleanSetting(securityGroup, "User Self Registration", "registration.allowloginbeforeconfirmation", false, "Enables self-registered users to login without clicking the activation link in the registration email.");
	public static final Setting<String> RegistrationCustomAttributes              = new StringSetting(securityGroup,  "User Self Registration", "registration.customuserattributes",         "name", "Attributes the registering user is allowed to provide. All other attributes are discarded. (eMail is always allowed)");

	public static final Setting<Integer> ConfirmationKeyPasswordResetValidityPeriod = new IntegerSetting(securityGroup, "Confirmation Key Validity", "confirmationkey.passwordreset.validityperiod", 30,    "Validity period (in minutes) of the confirmation key generated when a user resets his password. Default is 30.");
	public static final Setting<Integer> ConfirmationKeyRegistrationValidityPeriod  = new IntegerSetting(securityGroup, "Confirmation Key Validity", "confirmationkey.registration.validityperiod",  2880,  "Validity period (in minutes) of the confirmation key generated during self registration. Default is 2 days (2880 minutes)");
	public static final Setting<Boolean> ConfirmationKeyValidWithoutTimestamp       = new BooleanSetting(securityGroup, "Confirmation Key Validity", "confirmationkey.validwithouttimestamp",        false, "How to interpret confirmation keys without a timestamp");

	public static final Setting<Integer> LetsEncryptWaitBeforeAuthorization         = new IntegerSetting(securityGroup,  "Let's Encrypt", "letsencrypt.wait", 300, "Wait for this amount of seconds before trying to authorize challenge. Default is 300 seconds (5 minutes).");
	public static final Setting<String> LetsEncryptChallengeType                    = new ChoiceSetting(securityGroup,   "Let's Encrypt", "letsencrypt.challenge.type", "http", Settings.getStringsAsSet("http", "dns"), "Challenge type for Let's Encrypt authorization. Possible values are 'http' and 'dns'.");
	public static final Setting<String> LetsEncryptDomains                          = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.domains", "", "Space-separated list of domains to fetch and update Let's Encrypt certificates for");
	public static final Setting<String> LetsEncryptProductionServerURL              = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.production.server.url", "acme://letsencrypt.org", "URL of Let's Encrypt server. Default is 'acme://letsencrypt.org'");
	public static final Setting<String> LetsEncryptStagingServerURL                 = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.staging.server.url", "acme://letsencrypt.org/staging", "URL of Let's Encrypt staging server for testing only. Default is 'acme://letsencrypt.org/staging'.");
	public static final Setting<String> LetsEncryptUserKeyFilename                  = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.user.key.filename", "user.key", "File name of the Let's Encrypt user key. Default is 'user.key'.");
	public static final Setting<String> LetsEncryptDomainKeyFilename                = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.domain.key.filename", "domain.key", "File name of the Let's Encrypt domain key. Default is 'domain.key'.");
	public static final Setting<String> LetsEncryptDomainCSRFileName                = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.domain.csr.filename", "domain.csr", "File name of the Let's Encrypt CSR. Default is 'domain.csr'.");
	public static final Setting<String> LetsEncryptDomainChainFilename              = new StringSetting(securityGroup,   "Let's Encrypt", "letsencrypt.domain.chain.filename", "domain-chain.crt", "File name of the Let's Encrypt domain chain. Default is 'domain-chain.crt'.");
	public static final Setting<Integer> LetsEncryptKeySize                         = new IntegerSetting(securityGroup,  "Let's Encrypt", "letsencrypt.key.size", 2048, "Encryption key length. Default is 2048.");


	// oauth settings
	public static final Setting<String> OAuthServers            = new StringSetting(oauthGroup, "General", "oauth.servers", "github twitter linkedin google facebook auth0");

	public static final Setting<String> OAuthGithubAuthLocation   = new StringSetting(oauthGroup, "GitHub", "oauth.github.authorization_location", "https://github.com/login/oauth/authorize");
	public static final Setting<String> OAuthGithubTokenLocation  = new StringSetting(oauthGroup, "GitHub", "oauth.github.token_location", "https://github.com/login/oauth/access_token");
	public static final Setting<String> OAuthGithubClientId       = new StringSetting(oauthGroup, "GitHub", "oauth.github.client_id", "");
	public static final Setting<String> OAuthGithubClientSecret   = new StringSetting(oauthGroup, "GitHub", "oauth.github.client_secret", "");
	public static final Setting<String> OAuthGithubRedirectUri    = new StringSetting(oauthGroup, "GitHub", "oauth.github.redirect_uri", "/oauth/github/auth");
	public static final Setting<String> OAuthGithubUserDetailsUri = new StringSetting(oauthGroup, "GitHub", "oauth.github.user_details_resource_uri", "https://api.github.com/user/emails");
	public static final Setting<String> OAuthGithubErrorUri       = new StringSetting(oauthGroup, "GitHub", "oauth.github.error_uri", "/login");
	public static final Setting<String> OAuthGithubReturnUri      = new StringSetting(oauthGroup, "GitHub", "oauth.github.return_uri", "/");
	public static final Setting<String> OAuthGithubScope          = new StringSetting(oauthGroup, "GitHub", "oauth.github.scope", "user:email");

	public static final Setting<String> OAuthTwitterAuthLocation  = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.authorization_location", "https://api.twitter.com/oauth/authorize");
	public static final Setting<String> OAuthTwitterTokenLocation = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.token_location", "https://api.twitter.com/oauth/access_token");
	public static final Setting<String> OAuthTwitterClientId      = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.client_id", "");
	public static final Setting<String> OAuthTwitterClientSecret  = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.client_secret", "");
	public static final Setting<String> OAuthTwitterRedirectUri   = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.redirect_uri", "/oauth/twitter/auth");
	public static final Setting<String> OAuthTwitterErrorUri      = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.error_uri", "/login");
	public static final Setting<String> OAuthTwitterReturnUri     = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.return_uri", "/");
	public static final Setting<String> OAuthTwitterScope         = new StringSetting(oauthGroup, "Twitter", "oauth.twitter.scope", "");

	public static final Setting<String> OAuthLinkedInAuthLocation   = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.authorization_location", "https://www.linkedin.com/oauth/v2/authorization");
	public static final Setting<String> OAuthLinkedInTokenLocation  = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.token_location", "https://www.linkedin.com/oauth/v2/accessToken");
	public static final Setting<String> OAuthLinkedInClientId       = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.client_id", "");
	public static final Setting<String> OAuthLinkedInClientSecret   = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.client_secret", "");
	public static final Setting<String> OAuthLinkedInRedirectUri    = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.redirect_uri", "/oauth/linkedin/auth");
	public static final Setting<String> OAuthLinkedInUserDetailsUri = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.user_details_resource_uri", "https://api.linkedin.com/v2/emailAddress?q=members&projection=(elements*(handle~))");
	public static final Setting<String> OAuthLinkedInUserProfileUri = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.user_profile_resource_uri", "https://api.linkedin.com/v2/me");
	public static final Setting<String> OAuthLinkedInErrorUri       = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.error_uri", "/login");
	public static final Setting<String> OAuthLinkedInReturnUri      = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.return_uri", "/");
	public static final Setting<String> OAuthLinkedInScope          = new StringSetting(oauthGroup, "LinkedIn", "oauth.linkedin.scope", "r_liteprofile r_emailaddress");

	public static final Setting<String> OAuthGoogleAuthLocation   = new StringSetting(oauthGroup, "Google", "oauth.google.authorization_location", "https://accounts.google.com/o/oauth2/auth");
	public static final Setting<String> OAuthGoogleTokenLocation  = new StringSetting(oauthGroup, "Google", "oauth.google.token_location", "https://accounts.google.com/o/oauth2/token");
	public static final Setting<String> OAuthGoogleClientId       = new StringSetting(oauthGroup, "Google", "oauth.google.client_id", "");
	public static final Setting<String> OAuthGoogleClientSecret   = new StringSetting(oauthGroup, "Google", "oauth.google.client_secret", "");
	public static final Setting<String> OAuthGoogleRedirectUri    = new StringSetting(oauthGroup, "Google", "oauth.google.redirect_uri", "/oauth/google/auth");
	public static final Setting<String> OAuthGoogleUserDetailsUri = new StringSetting(oauthGroup, "Google", "oauth.google.user_details_resource_uri", "https://www.googleapis.com/oauth2/v3/userinfo");
	public static final Setting<String> OAuthGoogleErrorUri       = new StringSetting(oauthGroup, "Google", "oauth.google.error_uri", "/login");
	public static final Setting<String> OAuthGoogleReturnUri      = new StringSetting(oauthGroup, "Google", "oauth.google.return_uri", "/");
	public static final Setting<String> OAuthGoogleScope          = new StringSetting(oauthGroup, "Google", "oauth.google.scope", "email");

	public static final Setting<String> OAuthFacebookAuthLocation   = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.authorization_location", "https://www.facebook.com/dialog/oauth");
	public static final Setting<String> OAuthFacebookTokenLocation  = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.token_location", "https://graph.facebook.com/oauth/access_token");
	public static final Setting<String> OAuthFacebookClientId       = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.client_id", "");
	public static final Setting<String> OAuthFacebookClientSecret   = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.client_secret", "");
	public static final Setting<String> OAuthFacebookRedirectUri    = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.redirect_uri", "/oauth/facebook/auth");
	public static final Setting<String> OAuthFacebookUserDetailsUri = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.user_details_resource_uri", "https://graph.facebook.com/me?fields=id,name,email");
	public static final Setting<String> OAuthFacebookErrorUri       = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.error_uri", "/login");
	public static final Setting<String> OAuthFacebookReturnUri      = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.return_uri", "/");
	public static final Setting<String> OAuthFacebookScope          = new StringSetting(oauthGroup, "Facebook", "oauth.facebook.scope", "email");

	public static final Setting<String> OAuthAuth0AuthLocation   = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.authorization_location", "");
	public static final Setting<String> OAuthAuth0TokenLocation  = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.token_location", "");
	public static final Setting<String> OAuthAuth0ClientId       = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.client_id", "");
	public static final Setting<String> OAuthAuth0ClientSecret   = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.client_secret", "");
	public static final Setting<String> OAuthAuth0RedirectUri    = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.redirect_uri", "");
	public static final Setting<String> OAuthAuth0UserDetailsUri = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.user_details_resource_uri", "");
	public static final Setting<String> OAuthAuth0ErrorUri       = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.error_uri", "");
	public static final Setting<String> OAuthAuth0ReturnUri      = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.return_uri", "");
	public static final Setting<String> OAuthAuth0Scope          = new StringSetting(oauthGroup, "Auth0", "oauth.auth0.scope", "openid profile email");

	// LDAP settings
	public static final Setting<String> LDAPHost            = new StringSetting(ldapGroup,  "General", "ldap.host", "localhost");
	public static final Setting<Integer> LDAPPort           = new IntegerSetting(ldapGroup, "General", "ldap.port", 389);
	public static final Setting<Integer> LDAPConnectTimeout = new IntegerSetting(ldapGroup, "General", "ldap.connecttimeout", 1000, "Connection timeout in milliseconds");
	public static final Setting<String> LDAPBindDN          = new StringSetting(ldapGroup,  "General", "ldap.binddn", "", "DN that is used to authenticate synchronization");
	public static final Setting<String> LDAPSecret          = new StringSetting(ldapGroup,  "General", "ldap.secret", "");
	public static final Setting<Boolean> LDAPUseSSL         = new BooleanSetting(ldapGroup, "General", "ldap.usessl", false);
	public static final Setting<String> LDAPScope           = new StringSetting(ldapGroup,  "General", "ldap.scope", "SUBTREE");
	public static final Setting<String> LDAPPrimaryKey      = new StringSetting(ldapGroup,  "General", "ldap.primarykey", "dn", "Name of primary identification property of LDAP objects, must uniquely identify users and groups");
	public static final Setting<String> LDAPPropertyMapping = new StringSetting(ldapGroup,  "General", "ldap.propertymapping", "{ sn: name, email: eMail }", "Mapping from LDAP properties to Structr properties");
	public static final Setting<String> LDAPGroupNames      = new StringSetting(ldapGroup,  "General", "ldap.groupnames", "{ group: member, groupOfNames: member, groupOfUniqueNames: uniqueMember }", "LDAP objectclass tuples for group and member identification.");
	public static final Setting<Integer> LDAPUpdateInterval = new IntegerSetting(ldapGroup, "General", "ldap.updateinterval", 600, "Update interval for group synchronization in seconds");

	// miscellaneous settings
	public static final Setting<String> PaymentPaypalMode      = new StringSetting(miscGroup,  "Payment Options", "paypal.mode",         "");
	public static final Setting<String> PaymentPaypalUsername  = new StringSetting(miscGroup,  "Payment Options", "paypal.username",     "");
	public static final Setting<String> PaymentPaypalPassword  = new StringSetting(miscGroup,  "Payment Options", "paypal.password",     "");
	public static final Setting<String> PaymentPaypalSignature = new StringSetting(miscGroup,  "Payment Options", "paypal.signature",    "");
	public static final Setting<String> PaymentPaypalRedirect  = new StringSetting(miscGroup,  "Payment Options", "paypal.redirect",     "");
	public static final Setting<String> PaymentStripeApiKey    = new StringSetting(miscGroup,  "Payment Options", "stripe.apikey",       "");

	public static Collection<SettingsGroup> getGroups() {
		return groups.values();
	}

	public static SettingsGroup getGroup(final String key) {
		return groups.get(key);
	}

	public static Collection<Setting> getSettings() {
		return settings.values();
	}

	public static <T> Setting<T> getSetting(final String... keys) {
		return settings.get(StringUtils.join(toLowerCase(keys), "."));
	}

	public static <T> Setting<T> getCaseSensitiveSetting(final String... keys) {
		return settings.get(StringUtils.join(keys, "."));
	}

	public static Setting<String> getStringSetting(final String... keys) {

		final String key        = StringUtils.join(toLowerCase(keys), ".");
		Setting<String> setting = settings.get(key);

		return setting;
	}

	public static Setting<String> getOrCreateStringSetting(final String... keys) {

		final String key        = StringUtils.join(toLowerCase(keys), ".");
		Setting<String> setting = settings.get(key);

		if (setting == null) {

			setting = new StringSetting(miscGroup, key, null);
		}

		return setting;
	}

	public static Setting<Integer> getIntegerSetting(final String... keys) {

		final String key        = StringUtils.join(toLowerCase(keys), ".");
		Setting<Integer> setting = settings.get(key);

		return setting;
	}

	public static Setting<Integer> getOrCreateIntegerSetting(final String... keys) {

		final String key        = StringUtils.join(toLowerCase(keys), ".");
		Setting<Integer> setting = settings.get(key);

		if (setting == null) {

			setting = new IntegerSetting(miscGroup, key, null);
		}

		return setting;
	}

	public static Setting<Boolean> getBooleanSetting(final String... keys) {

		final String key         = StringUtils.join(toLowerCase(keys), ".");
		Setting<Boolean> setting = settings.get(key);

		return setting;
	}

	public static Setting<Boolean> getOrCreateBooleanSetting(final String... keys) {

		final String key         = StringUtils.join(toLowerCase(keys), ".");
		Setting<Boolean> setting = settings.get(key);

		if (setting == null) {

			setting = new BooleanSetting(miscGroup, key, null);
		}

		return setting;
	}

	public static Setting<?> createSettingForValue(final SettingsGroup group, final String key, final String value) {
		return createSettingForValue(group, key, value, false);
	}

	public static Setting<?> createSettingForValue(final SettingsGroup group, final String key, final String value, final boolean forceString) {

		if (value != null && !forceString) {

			// try to determine property value type, string, integer or boolean?
			final String lowerCaseValue = value.toLowerCase();

			// boolean
			if ("true".equals(lowerCaseValue) || "false".equals(lowerCaseValue)) {

				final Setting<Boolean> setting = new BooleanSetting(group, key);
				setting.setIsDynamic(true);
				setting.updateKey(key);
				setting.setValue(Boolean.parseBoolean(value));

				return setting;
			}

			// integer
			if (Settings.isNumeric(value)) {

				final Setting<Integer> setting = new IntegerSetting(group, key);
				setting.setIsDynamic(true);
				setting.updateKey(key);
				setting.setValue(Integer.parseInt(value));

				return setting;
			}
		}

		final Setting<String> setting = new StringSetting(group, key);
		setting.setIsDynamic(true);
		setting.updateKey(key);
		setting.setValue(value);

		return setting;
	}

	public static void storeConfiguration(final String fileName) throws IOException {

		try {

			PropertiesConfiguration.setDefaultListDelimiter('\0');

			final PropertiesConfiguration config = new PropertiesConfiguration();

			// store settings
			for (final Setting setting : settings.values()) {

				// story only modified settings and the super user password
				if (setting.isModified() || "superuser.password".equals(setting.getKey())) {

					config.setProperty(setting.getKey(), setting.getValue());
				}
			}

			config.save(fileName);

		} catch (ConfigurationException ex) {
			System.err.println("Unable to store configuration: " + ex.getMessage());
		}

	}

	public static void loadConfiguration(final String fileName) {

		try {

			PropertiesConfiguration.setDefaultListDelimiter('\0');

			final PropertiesConfiguration config = new PropertiesConfiguration(fileName);
			final Iterator<String> keys          = config.getKeys();

			while (keys.hasNext()) {

				final String key   = keys.next();
				final String lcKey = key.toLowerCase();
				final String value = trim(config.getString(key));
				Setting<?> setting = Settings.getSetting(lcKey);

				if (setting != null && setting.isDynamic()) {

					// unregister dynamic settings so the type can change (and cronExpressions are put in correct group)
					setting.unregister();
					setting = null;
				}

				if (setting != null) {

					setting.fromString(value);

				} else {

					// unknown setting => dynamic

					SettingsGroup targetGroup = miscGroup;

					// put key in cron group if it contains ".cronExpression"
					if (key.contains(".cronExpression")) {
						targetGroup = cronGroup;
					}

					// create new StringSetting for unknown key
					Settings.createSettingForValue(targetGroup, key, value, key.contains(Settings.ConnectionPassword.getKey()));
				}
			}

		} catch (ConfigurationException ex) {
			System.err.println("Unable to load configuration: " + ex.getMessage());
		}

	}

	public static String trim(final String value) {
		return StringUtils.trim(value);
	}

	public static void trim(final Properties properties) {
		for (Object k : properties.keySet()) {
			properties.put(k, trim((String) properties.get(k)));
		}
	}

	public static String getBasePath() {

		return checkPath(BasePath.getValue());

	}

	public static String getFullSettingPath(Setting<String> pathSetting) {

		return getBasePath() + checkPath(pathSetting.getValue());

	}

	private static String checkPath(final String path) {

		if (path.endsWith("/")) {
			return path;
		}

		return path + "/";
	}

	private static String[] toLowerCase(final String... input) {

		final ArrayList<String> lower = new ArrayList(input.length);

		for (final String i : input) {

			lower.add(i.toLowerCase());
		}

		return lower.toArray(new String[0]);
	}

	// ----- package methods -----
	static void registerGroup(final SettingsGroup group) {
		groups.put(group.getKey(), group);
	}

	static void registerSetting(final Setting setting) {

		final Setting oldSetting = settings.get(setting.getKey());

		if (oldSetting != null) {
			setting.setValue(oldSetting.getValue());
			oldSetting.unregister();
		}

		settings.put(setting.getKey(), setting);
	}

	static void unregisterSetting(final Setting setting) {
		settings.remove(setting.getKey());
	}


	public static Set<String> getStringsAsSet(final String... choices) {
		return new LinkedHashSet<>(Arrays.asList(choices));
	}

	public static Map<Integer, String> getTwoFactorSettingOptions() {
		final Map<Integer, String> options = new LinkedHashMap();
		options.put(0, "off");
		options.put(1, "optional");
		options.put(2, "forced");
		return options;
	}

	public static Map<Integer, String> getTwoFactorDigitsOptions() {
		final Map<Integer, String> options = new LinkedHashMap();
		options.put(6, "6 Digits");
		options.put(8, "8 Digits");
		return options;
	}

	public static boolean isNumeric(final String source) {

		try {

			final Integer value = Integer.parseInt(source);
			if (value.toString().equals(source)) {

				// value is not changed by parsing and toString()
				return true;
			}

		} catch (Throwable t) {}

		return false;
	}
}
