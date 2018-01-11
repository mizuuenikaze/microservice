/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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

import java.util.EnumSet;

import javax.servlet.DispatcherType;
import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.apache.camel.swagger.servlet.RestSwaggerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Replaces the web.xml configuration.
 *
 */
@Order(2)
public class SwaggerWebApplicationInitializer implements WebApplicationInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(SwaggerWebApplicationInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		LOG.info("Starting swagger servlet...");
		final ServletRegistration.Dynamic swaggerDispatcher = servletContext.addServlet("SwaggerServlet",
				RestSwaggerServlet.class);
		swaggerDispatcher.addMapping("/api-doc/*");
		swaggerDispatcher.setInitParameter("base.path", "api");
		swaggerDispatcher.setInitParameter("api.path", "api-doc");
		swaggerDispatcher.setInitParameter("api.version", "0.1");
		swaggerDispatcher.setInitParameter("api.title", "Api Docs");
		swaggerDispatcher.setInitParameter("api.description", "Rest Services via Camel");
		swaggerDispatcher.setInitParameter("apiContextIdListing", "true");

		final FilterRegistration.Dynamic filterRegistration = servletContext.addFilter("RestSwaggerCorsFilter",
				"org.apache.camel.swagger.servlet.RestSwaggerCorsFilter");

		// Modify the cors headers
		// filterRegistration.setInitParameter(name, value)

		filterRegistration.addMappingForServletNames(EnumSet.of(DispatcherType.REQUEST), true, "SwaggerServlet");

	}
}
