package com.muk.spring.config;

import javax.servlet.ServletContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.security.web.context.AbstractSecurityWebApplicationInitializer;

@Order(1)
public class SpringSecurityWebApplicationInitializer extends AbstractSecurityWebApplicationInitializer {
	private static final Logger LOG = LoggerFactory.getLogger(SpringSecurityWebApplicationInitializer.class);

	@Override
	protected void beforeSpringSecurityFilterChain(ServletContext servletContext) {
		LOG.info("Wiring spring security...");
	}
}
