package com.muk.app;

import java.io.IOException;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.DispatcherType;

import org.apache.camel.main.MainListenerSupport;
import org.apache.camel.main.MainSupport;
import org.apache.camel.spring.javaconfig.Main;
import org.eclipse.jetty.annotations.AnnotationConfiguration;
import org.eclipse.jetty.annotations.ClassInheritanceHandler;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.filter.DelegatingFilterProxy;

import com.muk.spring.config.RestletWebApplicationInitializer;
import com.muk.spring.config.SpringSecurityWebApplicationInitializer;
import com.muk.spring.config.SpringWebApplicationInitializer;

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
		main.setConfigClasses("com.muk.app.StandAloneRootConfig");
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
			System.setProperty("org.eclipse.jetty.server.webapp.parentLoaderPriority", "true");
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

				contextHandler.setConfigurations(new Configuration[] { new AnnotationConfiguration() {
					@Override
					public void preConfigure(WebAppContext context) throws Exception {
						super.preConfigure(context);

						// pre-populate class map since subclasses are
						// skipped.
						final ClassInheritanceMap map = new ClassInheritanceMap();
						final ConcurrentHashSet<String> set = new ConcurrentHashSet<>();
						set.add(SpringSecurityWebApplicationInitializer.class.getName());
						set.add(SpringWebApplicationInitializer.class.getName());
						set.add(RestletWebApplicationInitializer.class.getName());
						map.put(WebApplicationInitializer.class.getName(), set);
						context.setAttribute(CLASS_INHERITANCE_MAP, map);
						_classInheritanceHandler = new ClassInheritanceHandler(map);
					}
				} });
			} else {
				LOG.info("PROD MODDE...running from distribution artifacts.");
				contextHandler.setParentLoaderPriority(false);

				contextHandler.setConfigurations(new Configuration[] { new AnnotationConfiguration() });
			}

			contextHandler.setServer(jettyServer);
			contextHandler.setErrorHandler(new ErrorPageErrorHandler());

			contextHandler.addFilter(
					new FilterHolder(
							new DelegatingFilterProxy(AbstractSecurityWebApplicationInitializer.DEFAULT_FILTER_NAME)),
					"/*", EnumSet.allOf(DispatcherType.class));

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
				jettyServer.stop();
			} catch (final Exception e) {
				e.printStackTrace();
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
