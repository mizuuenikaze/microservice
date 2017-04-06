package com.muk.spring.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.restlet.ext.spring.SpringServerServlet;
import org.springframework.core.annotation.Order;
import org.springframework.web.WebApplicationInitializer;

/**
 * Replaces the web.xml configuration.
 * 
 */
@Order(3)
public class RestletWebApplicationInitializer implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {

		final ServletRegistration.Dynamic restletDispatcher = servletContext.addServlet("RestletServlet",
				SpringServerServlet.class);
		restletDispatcher.addMapping("/api/*");
		restletDispatcher.setInitParameter("org.restlet.component", "RestletComponent");
	}
}
