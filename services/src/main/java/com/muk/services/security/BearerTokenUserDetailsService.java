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
package com.muk.services.security;

import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.CachingOauthUserDetailsService;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.exchange.RestConstants;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class BearerTokenUserDetailsService implements CachingOauthUserDetailsService {
	private static final Logger LOG = LoggerFactory.getLogger(BearerTokenUserDetailsService.class);

	private UserCache userCache = new NullUserCache();

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Inject
	@Qualifier("hashService")
	private NonceService nonceService;

	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails user = userCache.getUserFromCache(username);

		if (user == null) {
			if (RestConstants.Rest.anonymousToken.equals(username)) {
				user = loadAnonymousUser();
			} else {
				throw new UsernameNotFoundException("reauthenticate");
			}
		} else if (!(user instanceof OauthUser)) {
			throw new UsernameNotFoundException("Unexpected user detail type.");
		} else if (!isValidToken(user.getPassword())) {
			refreshUser(user);
			user = userCache.getUserFromCache(username);
		}

		Assert.notNull(user, "UserDetailsService returned null for username " + username + ". "
				+ "This is an interface contract violation");

		return user;
	}

	@Override
	public Map<String, Object> loadByAuthorizationCode(String authorizationCode, String redirectUri) {
		final Map<String, Object> responsePayload = new HashMap<String, Object>();

		// request bearer token
		final URI authUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthTokenPath()).build().toUri();

		final URI userInfoUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthUserInfoPath()).build().toUri();

		ResponseEntity<JsonNode> response = null;

		final MultiValueMap<String, String> postBody = new LinkedMultiValueMap<String, String>();
		postBody.put("response_type", Collections.singletonList("token"));
		postBody.put("grant_type", Collections.singletonList("authorization_code"));
		postBody.put("code", Collections.singletonList(authorizationCode));
		postBody.put("redirect_uri", Collections.singletonList(redirectUri));

		try {
			response = restTemplate.exchange(authUrl, HttpMethod.POST, buildAuthRequest("Basic",
					securityCfgService.getOauthServiceClientId() + ":"
							+ keystoreService.getPBEKey(securityCfgService.getOauthServiceClientId()),
					postBody), JsonNode.class);
		} catch (final IOException ioEx) {
			LOG.error("Failed to get oauth client secret.", ioEx);
			response = new ResponseEntity<JsonNode>((JsonNode) null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to read client secret from keystore.", secEx);
			response = new ResponseEntity<JsonNode>((JsonNode) null, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final HttpClientErrorException httpEx) {
			response = new ResponseEntity<JsonNode>((JsonNode) null, httpEx.getStatusCode());

			if (httpEx instanceof HttpStatusCodeException) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
					LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
				}
			}
		}

		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			responsePayload.put("error",
					"Failed to request access token. " + response.getStatusCode().getReasonPhrase());
		} else {
			JsonQuery jq = null;
			String accessToken = null;

			try {
				jq = JsonQuery.compile(".access_token");
				accessToken = jq.apply(response.getBody()).get(0).textValue();
			} catch (final JsonQueryException ex) {
				responsePayload.put("error", "Failed jq " + ex.getMessage());
			}

			// Request user info
			ResponseEntity<JsonNode> infoResponse = null;

			try {
				infoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET,
						buildAuthRequest("Bearer", accessToken, null), JsonNode.class);
			} catch (final HttpClientErrorException httpEx) {
				response = new ResponseEntity<JsonNode>((JsonNode) null, httpEx.getStatusCode());

				if (httpEx instanceof HttpStatusCodeException) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
						LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
					}
				}
			}

			if (!HttpStatus.OK.equals(infoResponse.getStatusCode())) {
				responsePayload.put("error",
						"Failed to request user info. " + response.getStatusCode().getReasonPhrase());
			} else {
				// Munge a secondary bearer token
				String secondaryToken = null;

				try {
					secondaryToken = nonceService.generateHash(securityCfgService.getSalt(), accessToken);
				} catch (final InvalidKeyException invalidKeyEx) {
					responsePayload.put("error", "Failed creating secondary token. " + invalidKeyEx.getMessage());
				} catch (final NoSuchAlgorithmException noSuchAlgorithmEx) {
					responsePayload.put("error", "Failed creating secondary token. " + noSuchAlgorithmEx.getMessage());
				}

				if (responsePayload.get("error") == null) {
					try {
						jq = JsonQuery.compile(".refresh_token");
						final String refreshToken = jq.apply(response.getBody()).get(0).textValue();
						jq = JsonQuery.compile(".user_name");
						final String userName = jq.apply(infoResponse.getBody()).get(0).textValue();
						// save in cache
						userCache.putUserInCache(
								buildUser(accessToken, secondaryToken, refreshToken, userName, redirectUri));

						String firstName = null;
						String lastName = null;

						jq = JsonQuery.compile(".given_name,.family_name");
						final List<JsonNode> jqNodes = jq.apply(infoResponse.getBody());
						firstName = jqNodes.get(0).textValue();
						lastName = jqNodes.get(1).textValue();

						final ObjectNode userResponse = JsonNodeFactory.instance.objectNode();
						userResponse.put("token", secondaryToken);
						userResponse.put("firstName", firstName);
						userResponse.put("lastName", lastName);
						userResponse.put("userName", userName);
						responsePayload.put("json", userResponse);

					} catch (final JsonQueryException ex) {
						responsePayload.put("error", "Failed jq. " + ex.getMessage());
					}
				}
			}
		}

		return responsePayload;
	}

	public UserCache getUserCache() {
		return userCache;
	}

	public void setUserCache(UserCache userCache) {
		this.userCache = userCache;
	}

	protected UserDetails loadAnonymousUser() {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_ANONYMOUS"));
		return new OauthUser(RestConstants.Rest.anonymousToken, " ", true, true, true, true, authorities, null, null);
	}

	protected boolean isValidToken(String primaryToken) {
		final URI checkTokenUrl = UriComponentsBuilder.fromUriString(securityCfgService.getOauthServer())
				.path(securityCfgService.getOauthCheckTokenPath()).build().toUri();

		final MultiValueMap<String, String> postBody = new LinkedMultiValueMap<String, String>();
		if (LOG.isDebugEnabled()) {
			LOG.debug("primary token on check token lookup {}", primaryToken);
		}
		postBody.put("token", Collections.singletonList(primaryToken));

		ResponseEntity<String> response = null;

		try {
			response = restTemplate.exchange(checkTokenUrl, HttpMethod.POST, buildAuthRequest("Basic",
					securityCfgService.getOauthServiceClientId() + ":"
							+ keystoreService.getPBEKey(securityCfgService.getOauthServiceClientId()),
					postBody), String.class);

		} catch (final IOException ioEx) {
			LOG.error("Failed to get oauth client secret.", ioEx);
			response = new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final GeneralSecurityException secEx) {
			LOG.error("Failed to read client secret from keystore.", secEx);
			response = new ResponseEntity<String>("", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (final HttpClientErrorException httpEx) {
			response = new ResponseEntity<String>("", httpEx.getStatusCode());

			if (httpEx instanceof HttpStatusCodeException) {
				if (httpEx.getStatusCode() == HttpStatus.BAD_REQUEST
						&& ((HttpStatusCodeException) httpEx).getResponseBodyAsString().contains("Token has expired")) {
					response = new ResponseEntity<String>("", HttpStatus.UNAUTHORIZED);
				}

				if (LOG.isDebugEnabled()) {
					LOG.debug("Status Code: {}", httpEx.getStatusCode().value());
					LOG.debug("Server Message: {}", ((HttpStatusCodeException) httpEx).getResponseBodyAsString());
				}
			}
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("check_token response: {}", response.getBody());
		}
		return HttpStatus.OK.equals(response == null ? "" : response.getStatusCode());
	}

	protected void refreshUser(UserDetails user) {
		loadByAuthorizationCode(((OauthUser) user).getRefreshToken(), ((OauthUser) user).getRedirectUri());
	}

	protected UserDetails buildUser(String accessToken, String secondaryToken, String refreshToken, String userName,
			String redirectUri) {
		final List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		final OauthUser userDetails = new OauthUser(userName, accessToken, authorities, refreshToken, redirectUri);
		userDetails.setSecondaryToken(secondaryToken);

		return userDetails;
	}

	private HttpEntity<MultiValueMap<String, String>> buildAuthRequest(String authType, String value,
			MultiValueMap<String, String> postBody) {
		HttpEntity<MultiValueMap<String, String>> request = null;

		final MultiValueMap<String, String> customHeaders = new LinkedMultiValueMap<String, String>();
		customHeaders.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_UTF8_VALUE);
		customHeaders.add(HttpHeaders.AUTHORIZATION, authType + " "
				+ (authType.equals("Basic") ? nonceService.encode(value.getBytes(StandardCharsets.UTF_8)) : value));

		if (postBody != null) {
			customHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		}

		request = new HttpEntity<MultiValueMap<String, String>>(postBody, customHeaders);

		return request;
	}
}
