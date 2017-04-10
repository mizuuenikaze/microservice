package com.muk.spring.config;

import static org.apache.activemq.camel.component.ActiveMQComponent.activeMQComponent;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.jms.ConnectionFactory;

import org.apache.activemq.camel.component.ActiveMQComponent;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.activemq.spring.ActiveMQConnectionFactory;
import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.jms.JmsConfiguration;
import org.apache.camel.component.restlet.RestletComponent;
import org.apache.camel.osgi.SpringCamelContextFactory;
import org.osgi.framework.BundleContext;
import org.restlet.Component;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;
import org.springframework.osgi.context.BundleContextAware;

@Configuration
@PropertySources(value = { @PropertySource(value = "classpath:camel.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:${CONF_BASE}/conf/muk/camel.properties", ignoreResourceNotFound = true) })
public class CamelConfig extends MultiRouteCamelConfiguration implements InitializingBean, BundleContextAware {

	@Inject
	private Environment environment;

	private BundleContext bundleContext;

	public BundleContext getBundleContext() {
		return bundleContext;
	}

	@Override
	public void setBundleContext(BundleContext bundleContext) {
		this.bundleContext = bundleContext;
	}

	@Override
	protected CamelContext createCamelContext() throws Exception {
		final SpringCamelContextFactory factory = new SpringCamelContextFactory();
		factory.setApplicationContext(getApplicationContext());
		factory.setBundleContext(getBundleContext());

		return factory.createContext();
	}

	@Override
	protected void setupCamelContext(CamelContext camelContext) throws Exception {

		super.setupCamelContext(camelContext);

		// stream caching for soap calls?
		// camelContext.getStreamCachingStrategy().setSpoolThreshold(0);

		// activemq
		final ActiveMQComponent activemq = activeMQComponent();
		activemq.setConfiguration(jmsConfiguration());
		camelContext.addComponent("activemq", activemq);

	}

	@Override
	public void afterPropertiesSet() throws Exception {
	}

	@Bean
	@Override
	public List<RouteBuilder> multiRoute() {
		final List<RouteBuilder> routes = new ArrayList<RouteBuilder>();
		routes.add(mainRoute());
		routes.add(restRoute());

		return routes;
	}

	@Bean
	public RouteBuilder mainRoute() {
		final Router router = new Router();

		return router;
	}

	@Bean
	public RouteBuilder restRoute() {
		final RestRouter router = new RestRouter();

		return router;
	}

	@Bean(name = { "RestletComponent" })
	public Component restletComponent() {
		return new Component();
	}

	@Bean(name = { "RestletComponentService" })
	public RestletComponent restletComponentService() {
		return new RestletComponent(restletComponent());
	}

	@Bean(name = "jmsConnectionFactory")
	public ConnectionFactory jmsConnectionFactory() {
		final ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
		connectionFactory.setBrokerURL(MessageFormat.format(
				"vm://localhost?broker.persistent=true&broker.dataDirectory={0}&jms.prefetchPolicy.all=1",
				environment.getProperty("camel.activemq.dataDirectoryFile")));
		return connectionFactory;
	}

	@Bean(name = "pooledConnectionFactory", initMethod = "start", destroyMethod = "stop")
	public ConnectionFactory pooledConnectionFactory() {
		final PooledConnectionFactory connectionFactory = new PooledConnectionFactory();
		connectionFactory.setMaxConnections(8);
		connectionFactory.setConnectionFactory(jmsConnectionFactory());

		return connectionFactory;
	}

	@Bean(name = "jmsConfiguration")
	public JmsConfiguration jmsConfiguration() {
		final JmsConfiguration configuration = new JmsConfiguration();
		configuration.setConnectionFactory(pooledConnectionFactory());
		configuration.setConcurrentConsumers(2);

		return configuration;
	}
}
