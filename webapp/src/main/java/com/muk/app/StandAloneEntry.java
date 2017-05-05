/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.app;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.eclipse.jetty.webapp.WebInfConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Main class for a web application with embedded jetty. Pure restful api using
 * camel with the restlet component.
 *
 */
public class StandAloneEntry {
	private static final Logger LOG = LoggerFactory.getLogger(StandAloneEntry.class);
	private Main main;

	public static void main(String[] args) throws Exception {
		final StandAloneEntry entry = new StandAloneEntry();
		entry.boot(args);
	}

	public void boot(String[] args) throws Exception {
		final Events eventListener = new Events(LOG);
		main = new Main();
		main.addOption(main.new ParameterOption("hp", "httpPort", "Jetty listening port", "httpPort") {
			@Override
			protected void doProcess(String arg, String parameter, LinkedList<String> remainingArgs) {
				eventListener.setHttpPort(Integer.valueOf(parameter));
			}
		});
		main.addOption(main.new ParameterOption("x", "devMode", "Running in dev mode", "devMode") {
			@Override
			protected void doProcess(String arg, String parameter, LinkedList<String> remainingArgs) {
				eventListener.setDevMode(true);
			}
		});

		// orchestrate stuff
		main.addMainListener(eventListener);
		main.run(args);
	}

	public static class Events extends MainListenerSupport {
		private int httpPort;
		private boolean devMode;
		private Server jettyServer;
		private final Logger LOG;

		public Events(Logger logger) {
			this.httpPort = 9090;
			this.LOG = logger;
		}

		@Override
		public void beforeStart(MainSupport main) {
			LOG.info("Minimal standalone start...");
		}

		@Override
		public void afterStart(MainSupport main) {
			LOG.info("Starting jetty...");
			final List<Configuration> configurations = new ArrayList<Configuration>();

			final Server jettyServer = new Server();
			final ServerConnector scc = new ServerConnector(jettyServer);
			scc.setPort(getHttpPort());

			jettyServer.setConnectors(new Connector[] { scc });
			jettyServer.setStopAtShutdown(true);

			final WebAppContext contextHandler = new WebAppContext(null, "/proxied", null, null, null,
					new ErrorPageErrorHandler(), ServletContextHandler.SESSIONS | ServletContextHandler.NO_SECURITY);

			if (isDevMode()) {
				LOG.info("DEV MODE...running from source.");
				final List<Resource> classDirs = contextHandler.getMetaData().getWebInfClassesDirs();
				try {
					classDirs.add(Resource.newResource("./build/classes/"));
				} catch (final IOException e) {
					LOG.error("Failed adding webinf directory.", e);
				}

				contextHandler.getMetaData().setWebInfClassesDirs(classDirs);
				contextHandler.setResourceBase("src/main/webapp");
				contextHandler.setParentLoaderPriority(true);
			} else {
				LOG.info("PROD MODE...running from distribution artifacts.");
				contextHandler.setParentLoaderPriority(false);
				configurations.add(new WebInfConfiguration());
				contextHandler.setWar(this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath());
			}

			configurations.add(new AnnotationConfiguration() {
				@Override
				public void preConfigure(WebAppContext context) throws Exception {
					super.preConfigure(context);
					context.setLogUrlOnStart(true);

					// pre-populate class map since subclasses are
					// skipped.
					final ClassInheritanceMap map = new ClassInheritanceMap();
					final ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
					set.add("com.muk.spring.config.SpringSecurityWebApplicationInitializer");
					set.add("com.muk.spring.config.RestletWebApplicationInitializer");
					set.add("com.muk.spring.config.SwaggerWebApplicationInitializer");
					map.put("org.springframework.web.WebApplicationInitializer", set);
					context.setAttribute(CLASS_INHERITANCE_MAP, map);
					_classInheritanceHandler = new ClassInheritanceHandler(map);
				}
			});

			contextHandler.setConfigurations(configurations.toArray(new Configuration[configurations.size()]));

			contextHandler.setServer(jettyServer);
			contextHandler.setErrorHandler(new ErrorPageErrorHandler());

			jettyServer.setHandler(contextHandler);

			try

			{
				jettyServer.start();
				LOG.info("Jetty started on port: " + getHttpPort());
				jettyServer.join();
			} catch (final Exception e) {
				LOG.error("Failed to start jetty.", e);
			}

		}

		@Override
		public void beforeStop(MainSupport main) {
			try {
				if (jettyServer != null) {
					jettyServer.stop();
				}
			} catch (final Exception e) {
				LOG.error("Failed to stop jetty server.", e);
			}

			LOG.info("Jetty stopping...");
		}

		@Override
		public void afterStop(MainSupport main) {
			LOG.info("Bye...");
		}

		public int getHttpPort() {
			return httpPort;
		}

		public void setHttpPort(int httpPort) {
			this.httpPort = httpPort;
		}

		public boolean isDevMode() {
			return devMode;
		}

		public void setDevMode(boolean devMode) {
			this.devMode = devMode;
		}
	}

}
