package com.muk.spring.config;

import javax.servlet.Filter;

import org.springframework.core.annotation.Order;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import com.muk.services.configuration.CachingConfig;
import com.muk.services.configuration.ServiceConfig;
import com.muk.web.filter.HashifyingFilter;

/**
 * Replaces the web.xml configuration.
 * 
 */
@Order(2)
public class SpringWebApplicationInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { AppRootConfig.class, CachingConfig.class, ServiceConfig.class,
				SpringSecurityConfig.class, CamelConfig.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { SpringMvcConfig.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/view/*" };
	}

	@Override
	protected Filter[] getServletFilters() {
		return new Filter[] { new HashifyingFilter() };
	}

}
