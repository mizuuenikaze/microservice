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
import org.restlet.Component;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.core.env.Environment;

import com.muk.restlet.CustomRestletBinding;
import com.muk.services.facades.PaymentFacade;
import com.muk.services.facades.impl.DefaultPaymentFacade;

@Configuration
@PropertySources(value = { @PropertySource(value = "classpath:camel.properties", ignoreResourceNotFound = true),
		@PropertySource(value = "file:${CONF_BASE}/conf/muk/camel.properties", ignoreResourceNotFound = true) })
public class CamelConfig extends MultiRouteCamelConfiguration {

	@Inject
	private Environment environment;

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

	/* facades */
	@Bean
	public PaymentFacade paymentFacade() {
		return new DefaultPaymentFacade();
	}

	/* framework components */

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

	@Bean
	public CustomRestletBinding customRestletBinding() {
		return new CustomRestletBinding();
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
