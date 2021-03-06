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
package org.structr.rest.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.structr.api.config.Setting;
import org.structr.api.config.Settings;
import org.structr.api.config.SettingsGroup;
import org.structr.api.service.DatabaseConnection;
import org.structr.api.util.html.Attr;
import org.structr.api.util.html.Document;
import org.structr.api.util.html.InputField;
import org.structr.api.util.html.Tag;
import org.structr.api.util.html.attr.Href;
import org.structr.api.util.html.attr.Rel;
import org.structr.common.error.FrameworkException;
import org.structr.core.Services;
import org.structr.core.graph.ManageDatabasesCommand;

/**
 *
 */
public class ConfigServlet extends AbstractServletBase {

	private static final Logger logger                = LoggerFactory.getLogger(ConfigServlet.class);
	private static final Set<String> sessions         = new HashSet<>();
	private static final String MainUrl               = "/structr/";
	private static final String ConfigUrl             = "/structr/config";
	private static final String ConfigName            = "structr.conf";
	private static final String TITLE                 = "Structr Configuration Editor";

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		setCustomResponseHeaders(response);

		if (!isAuthenticated(request)) {

			// no trailing semicolon so we dont trip MimeTypes.getContentTypeWithoutCharset
			response.setContentType("text/html; charset=utf-8");

			try (final PrintWriter writer = new PrintWriter(response.getWriter())) {

				final Document doc = createLoginDocument(request, writer);
				doc.render();

				writer.append("\n");
				writer.flush();

			} catch (IOException ioex) {
				ioex.printStackTrace();
			}

		} else {

			if (request.getParameter("reload") != null) {

				// reload data
				Settings.loadConfiguration(ConfigName);

				// redirect
				response.sendRedirect(ConfigUrl);

			} else if (request.getParameter("reset") != null) {

				final String key = request.getParameter("reset");
				Setting setting  = Settings.getSetting(key);

				if (setting == null) {
					setting = Settings.getCaseSensitiveSetting(key);
				}

				if (setting != null) {

					if (setting.isDynamic()) {

						// remove
						setting.unregister();

					} else {

						// reset to default
						setting.setValue(setting.getDefaultValue());
					}
				}

				// serialize settings
				Settings.storeConfiguration(ConfigName);

				// redirect
				response.sendRedirect(ConfigUrl);

			} else if (request.getParameter("start") != null) {

				final String serviceName = request.getParameter("start");
				if (serviceName != null && isAuthenticated(request)) {

					try {
						Services.getInstance().startService(serviceName);

					} catch (FrameworkException fex) {

						response.setContentType("application/json");
						response.setStatus(fex.getStatus());
						response.getWriter().print(fex.toJSON());
						response.getWriter().flush();
						response.getWriter().close();

						return;
					}
				}

				// redirect
				response.sendRedirect(ConfigUrl + "#services");

			} else if (request.getParameter("stop") != null) {

				final String serviceName = request.getParameter("stop");
				if (serviceName != null && isAuthenticated(request)) {

					Services.getInstance().shutdownService(serviceName);
				}

				// redirect
				response.sendRedirect(ConfigUrl + "#services");

			} else if (request.getParameter("restart") != null) {

				final String serviceName = request.getParameter("restart");
				if (serviceName != null && isAuthenticated(request)) {

					new Thread(new Runnable() {

						@Override
						public void run() {

							try { Thread.sleep(1000); } catch (Throwable t) {}

							Services.getInstance().shutdownService(serviceName);

							try {
								Services.getInstance().startService(serviceName);

							} catch (FrameworkException fex) {

								logger.warn("Unable to start service '{}'", serviceName);
								logger.warn("", fex);
							}
						}
					}).start();
				}

				// redirect
				response.sendRedirect(ConfigUrl + "#services");

			} else if (request.getParameter("finish") != null) {

				// finish wizard
				Settings.SetupWizardCompleted.setValue(true);
				Settings.storeConfiguration(ConfigName);

				// redirect
				response.sendRedirect(MainUrl);

			} else if (request.getParameter("useDefault") != null) {

				// create default configuration
				final ManageDatabasesCommand cmd    = Services.getInstance().command(null, ManageDatabasesCommand.class);
				final String name                   = "neo-1";
				final String url                    = Settings.SampleConnectionUrl.getDefaultValue();
				final String username               = Settings.ConnectionUser.getDefaultValue();
				final String password               = Settings.ConnectionPassword.getDefaultValue();

				final DatabaseConnection connection = new DatabaseConnection();
				connection.setName(name);
				connection.setUrl(url);
				connection.setUsername(username);
				connection.setPassword(password);

				try {
					cmd.addConnection(connection, false);

				} catch (FrameworkException fex) {
					fex.printStackTrace();
				}

				// finish wizard
				Settings.SetupWizardCompleted.setValue(true);
				Settings.storeConfiguration(ConfigName);

				// make session valid
				authenticateSession(request);

				// redirect
				response.sendRedirect(ConfigUrl + "#databases");

			} else {

				// no trailing semicolon so we dont trip MimeTypes.getContentTypeWithoutCharset
				response.setContentType("text/html; charset=utf-8");

				try (final PrintWriter writer = new PrintWriter(response.getWriter())) {

					final Document doc = createConfigDocument(request, writer);
					doc.render();

					writer.append("\n");
					writer.flush();

				} catch (IOException ioex) {
					ioex.printStackTrace();
				}

			}
		}
	}

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {

		setCustomResponseHeaders(response);

		final String action   = request.getParameter("action");
		String redirectTarget = "";

		if (action != null) {

			switch (action) {

				case "login":

					if (StringUtils.isNoneBlank(Settings.SuperUserPassword.getValue(), request.getParameter("password")) && Settings.SuperUserPassword.getValue().equals(request.getParameter("password"))) {
						authenticateSession(request);
					}
					break;

				case "logout":
					invalidateSession(request);
					break;

			}

		} else if (isAuthenticated(request)) {

			// set redirect target
			redirectTarget = request.getParameter("active_section");

			// database connections form
			if ("/add".equals(request.getPathInfo())) {

				final ManageDatabasesCommand cmd    = Services.getInstance().command(null, ManageDatabasesCommand.class);
				final String name                   = request.getParameter("name");
				final String url                    = request.getParameter("url");
				final String username               = request.getParameter("username");
				final String password               = request.getParameter("password");
				final String connectNow             = request.getParameter("now");
				final DatabaseConnection connection = new DatabaseConnection();

				connection.setName(name);
				connection.setUrl(url);
				connection.setUsername(username);
				connection.setPassword(password);

				try {
					cmd.addConnection(connection, cmd.getConnections().isEmpty() && "true".equals(connectNow));

					// wizard finished
					Settings.SetupWizardCompleted.setValue(true);

					// make session valid
					authenticateSession(request);

				} catch (FrameworkException fex) {

					response.setContentType("application/json");
					response.setStatus(fex.getStatus());
					response.getWriter().print(fex.toJSON());
					response.getWriter().flush();
					response.getWriter().close();

					return;
				}

			} else {

				// check for REST action
				final String path = request.getPathInfo();
				if (StringUtils.isNotBlank(path)) {

					final String[] parts = StringUtils.split(path, "/");
					if (parts.length == 2) {

						final ManageDatabasesCommand cmd = Services.getInstance().command(null, ManageDatabasesCommand.class);
						final Map<String, Object> data   = new LinkedHashMap<>();
						final String name                = parts[0];
						final String restAction          = parts[1];

						// values for save action
						final String connectionUrl       = request.getParameter("url");
						final String connectionUsername  = request.getParameter("username");
						final String connectionPassword  = request.getParameter("password");

						data.put(DatabaseConnection.KEY_NAME,     name);
						data.put(DatabaseConnection.KEY_URL,      connectionUrl);
						data.put(DatabaseConnection.KEY_USERNAME, connectionUsername);
						data.put(DatabaseConnection.KEY_PASSWORD, connectionPassword);

						try {
							switch (restAction) {

								case "save":
									cmd.saveConnection(data);
									break;

								case "delete":
									cmd.removeConnection(data);
									break;

								case "connect":
									cmd.saveConnection(data);
									cmd.activateConnection(data);
									break;

								case "disconnect":
									cmd.deactivateConnections();
									break;
							}

						} catch (FrameworkException fex) {

							response.setContentType("application/json");
							response.setStatus(fex.getStatus());
							response.getWriter().print(fex.toJSON());
							response.getWriter().flush();
							response.getWriter().close();

							return;
						}
					}

				} else {

					// a configuration form was submitted
					for (final Entry<String, String[]> entry : request.getParameterMap().entrySet()) {

						final String value   = getFirstElement(entry.getValue());
						final String key     = entry.getKey();
						SettingsGroup parent = null;

						// skip internal group configuration parameter
						if (key.endsWith("._settings_group")) {
							continue;
						}

						// skip

						if ("active_section".equals(key)) {

							redirectTarget = value;
							continue;
						}

						Setting<?> setting = Settings.getSetting(key);

						if (setting != null && setting.isDynamic()) {

							// unregister dynamic settings so the type can change
							setting.unregister();
							setting = null;
						}

						if (setting == null) {

							if (key.contains(".cronExpression")) {

								parent = Settings.cronGroup;

							} else {

								// group specified?
								final String group = request.getParameter(key + "._settings_group");
								if (group != null) {

									parent = Settings.getGroup(group);
									if (parent == null) {

										// default to misc group
										parent = Settings.miscGroup;
									}

								} else {

									// fallback to misc group
									parent = Settings.miscGroup;
								}
							}

							setting = Settings.createSettingForValue(parent, key, value);
						}

						// store new value
						setting.fromString(value);
					}
				}
			}

			// serialize settings
			Settings.storeConfiguration(ConfigName);
		}

		response.sendRedirect(ConfigUrl + redirectTarget);
	}

	// ----- private methods -----
	private Document createConfigDocument(final HttpServletRequest request, final PrintWriter writer) {

		final boolean firstStart = !Settings.SetupWizardCompleted.getValue();
		final Document doc       = new Document(writer);
		final Tag body           = setupDocument(request, doc);
		final Tag form           = body.block("form").css("config-form").empty("input").attr(new Attr("type", "submit"), new Attr("disabled", "disabled")).css("hidden").parent();
		final Tag main           = form.block("div").id("main");
		final Tag tabs           = main.block("div").id("configTabs");
		final Tag menu           = tabs.block("ul").css("tabs-menu");

		// configure form
		form.attr(new Attr("action", ConfigUrl), new Attr("method", "post"));

		if (firstStart) {
			welcomeTab(menu, tabs);
		}

		databasesTab(menu, tabs);

		if (!firstStart) {

			// settings tabs
			for (final SettingsGroup group : Settings.getGroups()) {

				final String key  = group.getKey();
				final String name = group.getName();

				menu.block("li").block("a").id(key + "Menu").attr(new Attr("href", "#" + key)).block("span").text(name);

				final Tag container = tabs.block("div").css("tab-content").id(key);

				// let settings group render itself
				group.render(container);

				// stop floating
				container.block("div").attr(new Style("clear: both;"));
			}

			// services tab
			menu.block("li").block("a").id("servicesMenu").attr(new Attr("href", "#services")).block("span").text("Services");

			final Services services = Services.getInstance();
			final Tag container     = tabs.block("div").css("tab-content").id("services");

			container.block("h1").text("Services");

			final Tag table         = container.block("table").id("services-table");
			final Tag header        = table.block("tr");

			header.block("th").text("Service Name");
			header.block("th").attr(new Attr("colspan", "2"));

			for (final Class serviceClass : services.getRegisteredServiceClasses()) {

				final Set<String> serviceNames = new TreeSet<>();

				serviceNames.addAll(services.getServices(serviceClass).keySet());
				serviceNames.add("default");

				for (final String name : serviceNames) {

					final boolean running         = serviceClass != null ? services.isReady(serviceClass, name) : false;
					final String serviceClassName = serviceClass.getSimpleName() + "." + name;

					final Tag row  = table.block("tr");

					row.block("td").text(serviceClassName);

					if (running) {

						row.block("td").block("button").attr(new Type("button"), new OnClick("window.location.href='" + ConfigUrl + "?restart=" + serviceClassName + "';")).text("Restart");

						if ("HttpService".equals(serviceClassName)) {

							row.block("td");

						} else {

							row.block("td").block("button").attr(new Type("button"), new OnClick("window.location.href='" + ConfigUrl + "?stop=" + serviceClassName + "';")).text("Stop");
						}

						row.block("td");

					} else {

						row.block("td");
						row.block("td");
						row.block("td").block("button").attr(new Type("button"), new OnClick("window.location.href='" + ConfigUrl + "?start=" + serviceClassName + "';")).text("Start");
					}
				}
			}

			// stop floating
			container.block("div").attr(new Style("clear: both;"));

			// buttons
			final Tag buttons = form.block("div").css("buttons");

			buttons.block("button").attr(new Type("button")).id("new-entry-button").text("Add entry");
			buttons.block("button").attr(new Type("button")).id("reload-config-button").text("Reload configuration file");
			buttons.empty("input").css("default-action").attr(new Type("submit"), new Value("Save to structr.conf"));
		}

		// update active section so we can restore it when redirecting
		form.empty("input").attr(new Type("hidden"), new Name("active_section")).id("active_section");

		return doc;
	}

	private Document createLoginDocument(final HttpServletRequest request, final PrintWriter writer) {

		final Document doc = new Document(writer);
		final Tag body     = setupDocument(request, doc).css("login");

		final Tag loginBox = body.block("div").id("login").css("dialog").attr(new Style("display: block; margin: auto; margin-top: 200px;"));

		loginBox.block("i").attr(new Attr("title", "Structr Logo")).css("logo-login sprite sprite-structr_gray_100x27");
		loginBox.block("p").text("Welcome to the " + TITLE + ". Please log in with the <b>super- user</b> password which can be found in your structr.conf.");

		final Tag form     = loginBox.block("form").attr(new Attr("action", ConfigUrl), new Attr("method", "post"));
		final Tag table    = form.block("table");
		final Tag row1     = table.block("tr");

		row1.block("td").block("label").attr(new Attr("for", "passwordField")).text("Password:");
		row1.block("td").empty("input").attr(new Attr("autofocus", "tur")).id("passwordField").attr(new Type("password"), new Name("password"));

		final Tag row2     = table.block("tr");
		final Tag cell13   = row2.block("td").attr(new Attr("colspan", "2")).css("btn");
		final Tag button   = cell13.block("button").id("loginButton").attr(new Name("login"));

		button.block("i").css("sprite sprite-key");
		button.block("span").text(" Login");

		cell13.empty("input").attr(new Type("hidden"), new Name("action"), new Value("login"));

		return doc;
	}

	// ----- private methods -----
	private Tag setupDocument(final HttpServletRequest request, final Document doc) {

		final Tag head = doc.block("head");

		head.block("title").text(TITLE);
		head.empty("meta").attr(new Attr("http-equiv", "Content-Type"), new Attr("content", "text/html;charset=utf-8"));
		head.empty("meta").attr(new Name("viewport"), new Attr("content", "width=1024, user-scalable=yes"));
		head.empty("link").attr(new Rel("stylesheet"), new Href("/structr/css/lib/jquery-ui-1.10.3.custom.min.css"));
		head.empty("link").attr(new Rel("stylesheet"), new Href("/structr/css/main.css"));
		head.empty("link").attr(new Rel("stylesheet"), new Href("/structr/css/sprites.css"));
		head.empty("link").attr(new Rel("stylesheet"), new Href("/structr/css/config.css"));
		head.empty("link").attr(new Rel("icon"), new Href("favicon.ico"), new Type("image/x-icon"));
		head.block("script").attr(new Src("/structr/js/lib/jquery-3.3.1.min.js"));
		head.block("script").attr(new Src("/structr/js/icons.js"));
		head.block("script").attr(new Src("/structr/js/config.js"));

		final Tag body = doc.block("body");
		final Tag header = body.block("div").id("header");

		header.block("i").css("logo sprite sprite-structr-logo");
		final Tag links = header.block("div").id("menu").css("menu").block("ul");

		if (isAuthenticated(request)) {

			final Tag form = links.block("li").block("form").attr(new Attr("action", ConfigUrl), new Attr("method", "post"), new Style("display: none")).id("logout-form");

			form.empty("input").attr(new Type("hidden"), new Name("action"), new Value("logout"));
			links.block("a").text("Logout").attr(new Style("cursor: pointer"), new OnClick("$('#logout-form').submit();"));
		}

		return body;
	}

	private boolean isAuthenticated(final HttpServletRequest request) {

		if (!Settings.SetupWizardCompleted.getValue()) {
			return true;
		}

		final HttpSession session = request.getSession();
		if (session != null) {

			final String sessionId = session.getId();
			if (sessionId != null) {

				return sessions.contains(sessionId);

			} else {

				logger.warn("Cannot check HTTP session without session ID, ignoring.");
			}

		} else {

			logger.warn("Cannot check HTTP request, no session.");
		}

		return false;
	}

	private void welcomeTab(final Tag menu, final Tag tabs) {

		final ManageDatabasesCommand cmd   = Services.getInstance().command(null, ManageDatabasesCommand.class);
		final boolean databaseIsConfigured = !cmd.getConnections().isEmpty();
		final boolean passwordIsSet        = StringUtils.isNotBlank(Settings.SuperUserPassword.getValue());
		final Style fgGreen                = new Style("color: #81ce25;");
		final Style bgGreen                = new Style("background-color: #81ce25; color: #fff; border: 1px solid rgba(0,0,0,.125);");
		final String id                    = "welcome";

		menu.block("li").css("active").block("a").id(id + "Menu").attr(new Attr("href", "#" + id)).block("span").text("Start").css("active");

		final Tag container = tabs.block("div").css("tab-content").id(id).attr(new Style("display: block;"));
		final Tag body      = header(container, "Initial Configuration");

		body.block("p").text("This is the first startup in configuration-only mode.");
		body.block("p").text("To start the server and access the user interface, the following actions must be performed:");

		final Tag list  = body.block("ol");
		final Tag item1 = list.block("li").text("Set a <b>superuser</b> password");
		final Tag item2 = list.block("li").text("Configure a <b>database connection</b>");

		if (passwordIsSet) {
			item1.block("span").text(" &#x2714;").attr(fgGreen);
		}

		if (databaseIsConfigured) {
			item2.block("span").text(" &#x2714;").attr(fgGreen);
		}

		if (!passwordIsSet) {

			body.block("h3").text("Superuser password");

			final Tag pwd = body.block("p");
			pwd.empty("input").attr(new Name("superuser.password")).attr(new Type("password")).attr(new Attr("size", 40));
			pwd.empty("input").attr(new Type("submit")).attr(new Attr("value", "Save")).attr(bgGreen);

		} else {

			body.block("h3").text("Next step: ");

			if (databaseIsConfigured) {

				body.block("p").css("steps").block("button").attr(new Type("button")).text("Manage database connections").attr(new OnClick("$('#databasesMenu').click();"));

			} else {

				body.block("p").css("steps").block("button").attr(new Type("button")).text("Configure a database connection").attr(new OnClick("window.location.href='/structr/config#databases'; $('#databasesMenu').click();"));
			}

		}
	}

	private void databasesTab(final Tag menu, final Tag tabs) {

		final ManageDatabasesCommand cmd           = Services.getInstance().command(null, ManageDatabasesCommand.class);
		final List<DatabaseConnection> connections = cmd.getConnections();
		final String id                            = "databases";

		menu.block("li").block("a").id(id + "Menu").attr(new Attr("href", "#" + id)).block("span").text("Database Connections");

		final Tag container = tabs.block("div").css("tab-content").id(id);
		final Tag body      = header(container, "Database Connections");

		if (connections.isEmpty()) {

			body.block("p").text("There are currently no database connections configured. To use Structr, you have the following options:");
			
			final Tag div = body.block("div");
			
			final Tag leftDiv  = div.block("div").css("inline-block");
			leftDiv.block("button").css("default-action").attr(new Type("button")).text("Create new database connection").attr(new OnClick("$('.new-connection.collapsed').removeClass('collapsed')"));
			leftDiv.block("p").text("Configure Structr to connect to a running database.");
			
			final Tag rightDiv = div.block("div").css("inline-block");
			rightDiv.block("button").attr(new Type("button")).text("Start in demo mode").attr(new OnClick("window.location.href='" + ConfigUrl + "?finish';"));
			rightDiv.block("p").text("Start Structr in demo mode. Please note that in this mode any data will be lost when stopping the server.");

		} else {

			boolean hasActiveConnection = connections.stream().map(DatabaseConnection::isActive).reduce(false, (t, u) -> t || u);
			if (!hasActiveConnection) {

				body.block("p").text("There is currently no active database connection.");
			}
		}

		// database connections
		for (final DatabaseConnection connection : connections) {

			connection.render(body, ConfigUrl);
		}

		// new connection form should appear below existing connections
		//body.block("div").attr(new Attr("style", "clear: both;"));

		//body.block("h2").text("Add connection");

		final Tag div = body.block("div").css("connection app-tile new-connection collapsed");

		//div.block("h4").text("Add database connection");

		final Tag name = div.block("p");
		name.block("label").text("Name");
		name.add(new InputField(name, "text", "name-structr-new-connection", "", "Enter a connection name"));

		final Tag url = div.block("p");
		url.block("label").text("Connection URL");
		url.add(new InputField(url, "text", "url-structr-new-connection", "", "Enter URL"));

		final Tag user = div.block("p");
		user.block("label").text("Username");
		user.add(new InputField(user, "text", "username-structr-new-connection", "", "Enter username"));

		final Tag pass = div.block("p");
		pass.block("label").text("Password");
		pass.add(new InputField(pass, "password", "password-structr-new-connection", "", "Enter password"));

		if (connections.isEmpty()) {

			// allow user to prevent connecting immediately
			final Tag checkbox = div.block("p");
			final Tag label    = checkbox.block("label");
			label.empty("input").attr(new Attr("type", "checkbox"), new Attr("id", "connect-checkbox"), new Attr("checked", "checked"));
			label.block("span").text("Connect immediately");
		}

		final Tag buttons = div.block("p").css("buttons");
		buttons.block("button").attr(new Attr("type", "button")).text("Set Neo4j defaults").attr(new Attr("onclick", "setNeo4jDefaults(this);"));
		buttons.block("button").css("default-action").attr(new Attr("type", "button")).text("Add connection").attr(new Attr("onclick", "addConnection(this);"));

		div.block("div").id("status-structr-new-connection").css("warning warning-message hidden");
	}

	private Tag header(final Tag container, final String title) {

		final Tag div       = container.block("div");
		final Tag main      = div.block("div").css("config-group");

		main.block("h1").text(title);

		// stop floating
		container.block("div").attr(new Style("clear: both;"));

		return main;
	}

	private void authenticateSession(final HttpServletRequest request) {

		final HttpSession session = request.getSession();
		if (session != null) {

			final String sessionId = session.getId();
			if (sessionId != null) {

				sessions.add(sessionId);

			} else {

				logger.warn("Cannot authenticate HTTP session without session ID, ignoring.");
			}

		} else {

			logger.warn("Cannot authenticate HTTP request, no session.");
		}
	}

	private void invalidateSession(final HttpServletRequest request) {

		final HttpSession session = request.getSession();
		if (session != null) {

			final String sessionId = session.getId();
			if (sessionId != null) {

				sessions.remove(sessionId);

			} else {

				logger.warn("Cannot invalidate HTTP session without session ID, ignoring.");
			}

		} else {

			logger.warn("Cannot invalidate HTTP request, no session.");
		}
	}

	private String getFirstElement(final String[] values) {

		if (values != null && values.length == 1) {

			return values[0];
		}

		return null;
	}

	// ----- nested classes -----
	private static class Style extends Attr {

		public Style(final Object value) {
			super("style", value);
		}
	}

	private static class Src extends Attr {

		public Src(final Object value) {
			super("src", value);
		}
	}

	private static class Type extends Attr {

		public Type(final Object value) {
			super("type", value);
		}
	}

	private static class Name extends Attr {

		public Name(final Object value) {
			super("name", value);
		}
	}

	private static class Value extends Attr {

		public Value(final Object value) {
			super("value", value);
		}
	}

	private static class OnClick extends Attr {

		public OnClick(final Object value) {
			super("onclick", value);
		}
	}
}