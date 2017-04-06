package com.muk.spring.config;

import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.spring.javaconfig.CamelConfiguration;

public abstract class MultiRouteCamelConfiguration extends CamelConfiguration {

	@Override
	public List<RouteBuilder> routes() {
		return multiRoute();
	}

	/**
	 * Creates the multiple {@link RouteBuilder} to use in this configuration
	 */
	public abstract List<RouteBuilder> multiRoute();

}
