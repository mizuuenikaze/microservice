package com.muk.web.filter;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.savedrequest.Enumerator;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.muk.ext.core.ApplicationState;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.CryptoService;
import com.muk.services.api.MozuConfigurationService;

/**
 * Validates a hash value from mozu.
 *
 */
public class HashifyingFilter extends GenericFilterBean {
	private static final Logger LOG = LoggerFactory.getLogger(HashifyingFilter.class);

	private MozuConfigurationService mozuConfigurationService;
	private KeystoreService keystoreService;
	private NonceService nonceService;
	private UserDetailsService autoLoginUserDetailService;
	private CryptoService cryptoService;

	private ApplicationState appState;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		if (((HttpServletRequest) request).getPathInfo().startsWith("/embed")) {
			loadBeans(request);

			final ResettableStreamHttpServletRequest wrappedRequest = new ResettableStreamHttpServletRequest(
					(HttpServletRequest) request);

			final String queryStringDate = wrappedRequest.getParameter("dt");
			final String queryStringHash = wrappedRequest.getParameter("messageHash");
			String tenant = wrappedRequest.getParameter("x-vol-tenant");
			String userId = wrappedRequest.getParameter("userId");

			if (tenant == null) {
				tenant = wrappedRequest.getParameter("tenantId");
			}
			if (userId == null) {
				userId = "unknown";
			}

			final Cookie[] cookies = wrappedRequest.getCookies();

			if (cookies != null) {
				for (final Cookie cookie : cookies) {
					if ("mozuToken".equals(cookie.getName())) {
						try {
							final String[] cookieValues = cryptoService.decrypt(cookie.getValue()).split("\\|");

							if (cookieValues == null || cookieValues.length != 2) {
								cookie.setMaxAge(0);
								((HttpServletResponse) response).addCookie(cookie);
								((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
								return;
							}

							tenant = cookieValues[0];
							userId = cookieValues[1];
						} catch (final NullPointerException nullEx) {
							cookie.setMaxAge(0);
							((HttpServletResponse) response).addCookie(cookie);
							((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
							return;
						}

						break;
					}
				}
			}

			// Disable the app if we don't know about it
			if (!appState.exists(Integer.valueOf(tenant))) {
				final Map<String, String> headers = new HashMap<String, String>();


				try {

				} catch (final Exception apiEx) {
					LOG.error("Failed to disable tenant " + tenant, apiEx);
					((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
					return;
				}
			}

			if (!isAuthenticated(tenant, userId, queryStringDate, queryStringHash,
					IOUtils.toString(wrappedRequest.getInputStream(), StandardCharsets.ISO_8859_1.name()),
					(HttpServletResponse) response, cryptoService.encrypt(tenant + "|" + userId))) {
				((HttpServletResponse) response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}

			wrappedRequest.resetInputStream();
			chain.doFilter(wrappedRequest, response);
		} else {
			chain.doFilter(request, response);
		}
	}

	private boolean isAuthenticated(String tenant, String userId, String queryStringDate, String queryStringHash,
			String body, HttpServletResponse response, String cookieValue) {
		boolean authenticated = false;
		final Map<String, String> headers = new HashMap<String, String>();


		final Cookie mozuCookie = new Cookie("mozuToken", cookieValue);
		mozuCookie.setMaxAge(86400);

		if (queryStringDate != null && queryStringHash != null) {

			try {
				final String sharedSecret = keystoreService.getPBEKey(mozuConfigurationService.getAppId());
				final String doubleSecret = nonceService.generateHash(sharedSecret + sharedSecret);
				final String payloadHash = nonceService.generateHash(doubleSecret.concat(queryStringDate).concat(body));

				if (isValidMozuAdminRequest(payloadHash, queryStringHash, queryStringDate)) {
					autoLogin(userId);


					authenticated = true;
				} else {
					mozuCookie.setMaxAge(0);
				}

				response.addCookie(mozuCookie);

			} catch (final IOException ioEx) {
				LOG.error("Failed to lookup secret.", ioEx);
			} catch (final GeneralSecurityException secEx) {
				LOG.error("Failed to lookup secret.", secEx);
			}

		} else if (tenant != null && userId != null) {
			autoLogin(userId);

			authenticated = true;
		}

		return authenticated;
	}

	private boolean isValidMozuAdminRequest(String payloadHash, String messageHash, String dt) {
		final boolean isValid = messageHash.equals(payloadHash);

		return isValid;
	}

	private void autoLogin(String userId) {
		if (userId != null) {
			UserDetails user = null;

			try {
				user = autoLoginUserDetailService.loadUserByUsername(userId);
			} catch (final UsernameNotFoundException e) {
				user = autoLoginUserDetailService.loadUserByUsername("unknown");
			}

			final Authentication auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(auth);
		}
	}

	private void loadBeans(ServletRequest request) {
		if (mozuConfigurationService == null) {
			final ServletContext servletContext = request.getServletContext();
			final WebApplicationContext webApplicationContext = WebApplicationContextUtils
					.getWebApplicationContext(servletContext);

			mozuConfigurationService = (MozuConfigurationService) webApplicationContext
					.getBean("mozuConfigurationService");
			keystoreService = (KeystoreService) webApplicationContext.getBean("generalKeystoreService");
			nonceService = (NonceService) webApplicationContext.getBean("hashService");
			autoLoginUserDetailService = (UserDetailsService) webApplicationContext
					.getBean("autoLoginUserDetailService");
			cryptoService = (CryptoService) webApplicationContext.getBean("cryptoService");


			appState = (ApplicationState) webApplicationContext.getBean("applicationState");
		}
	}

	private static class ResettableStreamHttpServletRequest extends HttpServletRequestWrapper {

		private byte[] rawData;
		private Map<String, String[]> parameterMap;
		private final HttpServletRequest request;
		private final ResettableServletInputStream servletStream;

		public ResettableStreamHttpServletRequest(HttpServletRequest request) {
			super(request);
			this.request = request;
			this.servletStream = new ResettableServletInputStream();
		}

		public void resetInputStream() {
			servletStream.stream = new ByteArrayInputStream(rawData);
		}

		@Override
		public ServletInputStream getInputStream() throws IOException {
			parseRequest();
			return servletStream;
		}

		@Override
		public BufferedReader getReader() throws IOException {
			parseRequest();
			return new BufferedReader(new InputStreamReader(servletStream));
		}

		@Override
		public Enumeration<String> getParameterNames() {
			final Set<String> names = new HashSet<String>();
			names.addAll(getParameterMap().keySet());

			return new Enumerator<String>(names);
		}

		@Override
		public String[] getParameterValues(String name) {
			return getParameterMap().get(name);
		}

		@Override
		public String getParameter(String name) {
			final Map<String, String[]> parameterMap = getParameterMap();
			final String[] values = parameterMap.get(name);

			if (values != null && values.length > 0) {
				return values[0];
			}

			return null;
		}

		@Override
		public Map<String, String[]> getParameterMap() {

			try {
				parseRequest();

				if (parameterMap == null) {
					parameterMap = new HashMap<String, String[]>();
					parseQueryString(parameterMap);
					parseData(parameterMap);
				}
			} catch (final IOException e) {
				LOG.error("Failed to parse request", e);
			}

			return parameterMap;
		}

		private void parseRequest() throws IOException {
			if (rawData == null) {

				rawData = IOUtils.toByteArray(this.request.getReader(), StandardCharsets.UTF_8);
				servletStream.stream = new ByteArrayInputStream(rawData);
			}
		}

		private void parseQueryString(Map<String, String[]> map) {
			final String queryString = request.getQueryString();

			if (StringUtils.isNotBlank(queryString)) {
				final Map<String, LinkedList<String>> tempMap = new HashMap<String, LinkedList<String>>();
				final StringTokenizer tokens = new StringTokenizer(queryString, "&");

				while (tokens.hasMoreTokens()) {
					try {
						parseTuple(tokens.nextToken().split("="), tempMap, true);
					} catch (final UnsupportedEncodingException e) {
						LOG.error("Failed to parse parameters", e);
					}
				}

				final HashMap<String, String[]> finalMap = new HashMap<String, String[]>(tempMap.size() * 2);
				LinkedList<String> list = null;

				for (final String key : tempMap.keySet()) {
					list = tempMap.get(key);
					finalMap.put(key, list.toArray(new String[list.size()]));
				}

				map.putAll(finalMap);
			}
		}

		private void parseData(Map<String, String[]> map) {
			String encoding = request.getCharacterEncoding();
			final boolean decode = request.getContentType() != null
					&& request.getContentType().startsWith(MediaType.APPLICATION_FORM_URLENCODED_VALUE);

			if (encoding == null) {
				encoding = StandardCharsets.UTF_8.name();
			}

			String payload = null;

			try {
				payload = new String(rawData, encoding);
			} catch (final UnsupportedEncodingException e) {
				LOG.error("Failed to encode rawData", e);
			}

			if (StringUtils.isNotBlank(payload)) {
				final Map<String, LinkedList<String>> tempMap = new HashMap<String, LinkedList<String>>();
				final StringTokenizer tokens = new StringTokenizer(payload, "&");

				while (tokens.hasMoreTokens()) {
					try {
						parseTuple(tokens.nextToken().split("="), tempMap, decode);
					} catch (final UnsupportedEncodingException e) {
						LOG.error("Failed to parse data", e);
					}
				}

				final HashMap<String, String[]> finalMap = new HashMap<String, String[]>(tempMap.size() * 2);
				LinkedList<String> list = null;

				for (final String key : tempMap.keySet()) {
					list = tempMap.get(key);
					finalMap.put(key, list.toArray(new String[list.size()]));
				}

				map.putAll(finalMap);
			}
		}

		private void parseTuple(String[] terms, Map<String, LinkedList<String>> map, boolean urlDecode)
				throws UnsupportedEncodingException {
			String name = null;
			String value = null;
			LinkedList<String> list = null;

			name = urlDecode ? URLDecoder.decode(terms[0], StandardCharsets.UTF_8.name()) : terms[0];

			if (terms.length > 1) {
				value = urlDecode ? URLDecoder.decode(terms[1], StandardCharsets.UTF_8.name()) : terms[1];
			} else {
				value = name;
			}

			list = map.get(name);

			if (list == null) {
				list = new LinkedList<String>();
				map.put(name, list);
			}

			list.add(value);
		}

		private class ResettableServletInputStream extends ServletInputStream {

			private ByteArrayInputStream stream;

			@Override
			public int read() throws IOException {
				return stream.read();
			}

			@Override
			public boolean isFinished() {
				return stream.available() == 0;
			}

			@Override
			public boolean isReady() {
				return true;
			}

			@Override
			public void setReadListener(ReadListener readListener) {
				throw new UnsupportedOperationException();
			}
		}
	}
}
