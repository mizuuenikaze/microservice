package com.muk.services.security;

import java.net.HttpCookie;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.api.UaaLoginService;

/**
 *
 * Implements the login sequence with UAA to provide a custom login flow without spring mvc.
 * <p>
 * The UAA login server requirements are based on a manual browser login. However with an SPA, it is not unreasonable to
 * want a highly customized login flow. Since this would be via xhr or fetch, there are header and cookie restrictions
 * that make it impossible to fulfill the spring mvc requirements to successfully login. This simplifies the login
 * process for an SPA to satisfy the auth code oauth flow.
 * <p>
 * Supports multiple clients as long as an entry is available in the internal keystore.
 *
 */
public class DefaultUaaLoginService implements UaaLoginService {
	private static final String CSRF = "X-Uaa-Csrf";
	private static final Pattern csrfPat = Pattern.compile("X-Uaa-Csrf=(?<csrf>[^;]+)");

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService cfgService;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> loginForClient(String username, String password, String clientId,
			UriComponents inUrlComponents) {
		final Map<String, Object> responsePayload = new HashMap<String, Object>();

		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(cfgService.getOauthServer());

		// login for csrf
		final UriComponents loginUri = uriBuilder.cloneBuilder().pathSegment("login").build();

		ResponseEntity<String> response = exchangeForType(loginUri.toUriString(), HttpMethod.GET, null, headers,
				String.class);

		final List<String> cookies = new ArrayList<String>();
		cookies.addAll(response.getHeaders().get(HttpHeaders.SET_COOKIE));

		final MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("username", username);
		formData.add("password", password);
		formData.add(CSRF, getCsrf(cookies));

		headers.put(HttpHeaders.COOKIE, translateInToOutCookies(cookies));
		headers.add(HttpHeaders.REFERER, loginUri.toUriString());

		// login.do
		response = exchangeForType(uriBuilder.cloneBuilder().pathSegment("login.do").build().toUriString(),
				HttpMethod.POST, formData, headers, String.class);

		if (response.getStatusCode() != HttpStatus.FOUND
				|| response.getHeaders().getFirst(HttpHeaders.LOCATION).contains("login")) {
			responsePayload.put("error", "bad credentials");
			return responsePayload;
		}

		removeCookie(cookies, "X-Uaa-Csrf");
		cookies.addAll(response.getHeaders().get(HttpHeaders.SET_COOKIE));
		removeExpiredCookies(cookies);
		headers.remove(HttpHeaders.REFERER);
		headers.put(HttpHeaders.COOKIE, translateInToOutCookies(cookies));

		// authorize
		final ResponseEntity<JsonNode> authResponse = exchangeForType(
				uriBuilder.cloneBuilder().pathSegment("oauth").pathSegment("authorize")
						.queryParam("response_type", "code").queryParam("client_id", clientId)
						.queryParam("redirect_uri", inUrlComponents.toUriString()).build().toUriString(),
				HttpMethod.GET, null, headers, JsonNode.class);

		if (authResponse.getStatusCode() == HttpStatus.OK) {
			removeCookie(cookies, "X-Uaa-Csrf");
			cookies.addAll(authResponse.getHeaders().get(HttpHeaders.SET_COOKIE));
			// return approval data
			final List<HttpCookie> parsedCookies = new ArrayList<HttpCookie>();

			for (final String cookie : cookies) {
				parsedCookies.add(HttpCookie.parse(cookie).get(0));
			}

			responsePayload.put(HttpHeaders.SET_COOKIE, new ArrayList<String>());

			for (final HttpCookie parsedCookie : parsedCookies) {
				if (!parsedCookie.getName().startsWith("Saved-Account")) {
					parsedCookie.setPath(inUrlComponents.getPath());
					((List<String>) responsePayload.get(HttpHeaders.SET_COOKIE)).add(httpCookieToString(parsedCookie));
				}
			}

			responsePayload.put("json", authResponse.getBody());
		} else {
			// get auth_code from Location Header
			responsePayload.put("code", authResponse.getHeaders().getLocation().getQuery().split("=")[1]);
		}

		return responsePayload;
	}

	@Override
	public String approveClient(String approvalQuery, String cookie) {
		final UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(cfgService.getOauthServer());
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));

		final StringTokenizer cookieTokenizer = new StringTokenizer(cookie, "; ");
		while (cookieTokenizer.hasMoreTokens()) {
			headers.add(HttpHeaders.COOKIE, cookieTokenizer.nextToken());
		}

		final MultiValueMap<String, String> formData = new LinkedMultiValueMap<String, String>();
		for (final String pair : approvalQuery.split("&")) {
			final String[] nv = pair.split("=");
			formData.add(nv[0], nv[1]);
		}
		formData.add("X-Uaa-Csrf", getCsrf(headers.get(HttpHeaders.COOKIE)));

		final UriComponents loginUri = uriBuilder.cloneBuilder().pathSegment("oauth").pathSegment("authorize").build();

		final ResponseEntity<String> response = exchangeForType(loginUri.toUriString(), HttpMethod.POST, formData,
				headers, String.class);

		if (approvalQuery.contains("false")) {
			return null; // approval declined.
		}

		// accepted, but location contains error
		if (response.getHeaders().getLocation().getQuery().startsWith("error")) {
			throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, response.getHeaders().getLocation().getQuery());
		}

		// accepted with related auth code
		return response.getHeaders().getLocation().getQuery().split("=")[1];
	}

	private <T> ResponseEntity<T> exchangeForType(String url, HttpMethod method, MultiValueMap<String, String> formData,
			HttpHeaders headers, Class<T> returnType) {
		if (headers.getContentType() == null) {
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		}

		final ResponseEntity<T> response = restTemplate.exchange(url, method,
				new HttpEntity<MultiValueMap<String, String>>(formData, headers), returnType);

		return response;
	}

	private String getCsrf(List<String> cookies) {
		for (final String cookie : cookies) {
			if (cookie.contains(CSRF)) {
				final Matcher m = csrfPat.matcher(cookie);
				if (m.find()) {
					return m.group("csrf");
				}
			}
		}

		return StringUtils.EMPTY;
	}

	private void removeCookie(List<String> cookies, String name) {
		final Iterator<String> iterator = cookies.iterator();

		while (iterator.hasNext()) {
			final String current = iterator.next();
			if (current.startsWith(name)) {
				iterator.remove();
			}
		}
	}

	private void removeExpiredCookies(List<String> cookies) {
		final Iterator<String> iterator = cookies.iterator();

		while (iterator.hasNext()) {
			final String current = iterator.next();
			if (current.toLowerCase().indexOf("max-age=0") >= 0) {
				iterator.remove();
			}
		}
	}

	private String httpCookieToString(HttpCookie cookie) {
		final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC).plusSeconds(cookie.getMaxAge());
		final String cookieExpires = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);
		final StringBuilder cookieBuilder = new StringBuilder();
		cookieBuilder.append(cookie.getName()).append("=").append(cookie.getValue()).append(";path=")
				.append(cookie.getPath()).append(";max-age=").append(cookie.getMaxAge()).append(";expires=")
				.append(cookieExpires);

		if (cookie.isHttpOnly()) {
			cookieBuilder.append(";HttpOnly");
		}

		return cookieBuilder.toString();
	}

	private List<String> translateInToOutCookies(List<String> inCookies) {
		final List<String> outCookies = new ArrayList<String>();

		for (final String inCookie : inCookies) {
			final HttpCookie httpCookie = HttpCookie.parse(inCookie).get(0);
			final StringBuilder cookieBuilder = new StringBuilder();
			cookieBuilder.append(httpCookie.getName()).append("=").append(httpCookie.getValue());
			outCookies.add(cookieBuilder.toString());
		}

		return outCookies;
	}
}
