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

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.restlet.ext.spring.SpringServerServlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.AbstractContextLoaderInitializer;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.muk.security.AuthEventListener;
import com.muk.services.configuration.CachingConfig;
import com.muk.services.configuration.ServiceConfig;

/**
 * Replaces the web.xml configuration.
 *
 */
@Order(1)
public class RestletWebApplicationInitializer extends AbstractContextLoaderInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(RestletWebApplicationInitializer.class);

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		LOG.info("Starting restlet servlet...");
		super.onStartup(servletContext);

		final ServletRegistration.Dynamic restletDispatcher = servletContext.addServlet("RestletServlet",
				SpringServerServlet.class);
		restletDispatcher.addMapping("/*");
		restletDispatcher.setInitParameter("org.restlet.component", "RestletComponent");
		restletDispatcher.setAsyncSupported(false);
	}

	@Override
	protected WebApplicationContext createRootApplicationContext() {

		final AnnotationConfigWebApplicationContext rootAppContext = new AnnotationConfigWebApplicationContext();
		rootAppContext.register(new Class<?>[] { AppRootConfig.class, CachingConfig.class, ServiceConfig.class,
				SpringSecurityConfig.class, CamelConfig.class });

		rootAppContext.addApplicationListener(new AuthEventListener());
		return rootAppContext;

	}

}
