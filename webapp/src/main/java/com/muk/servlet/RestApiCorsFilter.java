package com.muk.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.apache.camel.swagger.servlet.RestSwaggerCorsFilter;

public class RestApiCorsFilter extends RestSwaggerCorsFilter {

	private final Map<String, String> corsHeaders = new HashMap<String, String>();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		super.init(filterConfig);

		final String s = filterConfig.getInitParameter("Access-Control-Allow-Credentials");
		if (s != null) {
			corsHeaders.put("Access-Control-Allow-Credentials", s);
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		setupExtraCorsHeaders((HttpServletResponse) response, corsHeaders);
		super.doFilter(request, response, chain);
	}

	private static void setupExtraCorsHeaders(HttpServletResponse response, Map<String, String> corsHeaders) {
		// use default value if none has been configured
		String allowCredentials = corsHeaders != null ? corsHeaders.get("Access-Control-Allow-Credentials") : null;
		if (allowCredentials == null) {
			allowCredentials = "false";
		}

		response.setHeader("Access-Control-Allow-Credentials", allowCredentials);

	}
}
