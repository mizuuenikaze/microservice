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
package com.muk.services.commerce;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.muk.services.api.CrudListService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractListMozuService<T> implements CrudListService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractListMozuService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();
	private static Pattern mukErrorCode = Pattern.compile("errorCode\":\"(\\w+)");

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public boolean insert(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		boolean success = true;
		ResponseEntity<Object> response = null;
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			response = restTemplateBuilder.exchange(apiPath, HttpMethod.POST, httpEntity, Object.class, urlVariableMap);
			success = response != null;
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "add", apiPath, responseType.getClass().getName())) {
				response = restTemplateBuilder.exchange(apiPath, HttpMethod.POST, httpEntity, Object.class,
						urlVariableMap);
				success = response != null;
			}
		} catch (final RestClientException e) {
			logException("add", apiPath, responseType.getClass().getName(), e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean update(String apiPath, List<T> entity) throws Exception {
		final boolean success = true;
		final String entityId = getEntityId(entity);
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			if (entityId != null) {
				if (!apiPath.contains("documentpublishing")) {
					urlVariableMap.put("id", Collections.singletonList(entityId));
				} else {
					final List<String> docLists = new ArrayList<String>();
					docLists.add("productSharedContent@SSadd");
					docLists.add("categorySharedContent@SSadd");

					urlVariableMap.put("documentLists", docLists);
				}
			}

			restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, Object.class, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "update", apiPath, entityId)) {
				restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, Object.class, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("update", apiPath, entityId, e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean update(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		final boolean success = true;
		final String entityId = getEntityId(entity);
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		ResponseEntity<List<T>> response = null;

		try {
			if (entityId != null) {
				urlVariableMap.put("id", Collections.singletonList(entityId));
			}

			response = restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "update", apiPath, entityId)) {
				response = restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, responseType,
						urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("update", apiPath, entityId, e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean delete(String apiPath, List<T> entity) throws Exception {
		final String entityId = getEntityId(entity);
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			if (entityId != null) {
				urlVariableMap.put("id", Collections.singletonList(entityId));
			}

			restTemplateBuilder.exchange(apiPath, HttpMethod.DELETE, httpEntity, Object.class, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "delete", apiPath, entityId)) {
				restTemplateBuilder.exchange(apiPath, HttpMethod.DELETE, httpEntity, Object.class, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("delete", apiPath, entityId, e);
			throw e;
		}

		return true;
	}

	@Override
	public List<T> read(String apiPath, List<T> entityTemplate, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		ResponseEntity<List<T>> response = null;
		final String entityId = getEntityId(entityTemplate);
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		if (entityId != null) {
			urlVariableMap.put("id", Collections.singletonList(entityId));
		}

		try {
			response = restTemplateBuilder.exchange(apiPath, HttpMethod.GET, null, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "get", apiPath, responseType.getClass().getName())) {
				response = restTemplateBuilder.exchange(apiPath, HttpMethod.GET, null, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("get", apiPath, responseType.getClass().getName(), e);
			throw e;
		}

		if (response != null) {
			return response.getBody();
		}

		return Collections.emptyList();
	}

	@Override
	public boolean upsert(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		boolean success = true;
		final String entityId = getEntityId(entity);
		if (StringUtils.isBlank(entityId)) {
			success = insert(apiPath, entity, responseType);
		} else {
			try {
				success = update(apiPath, entity);
			} catch (final HttpClientErrorException clientEx) {
				if (clientEx.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					success = insert(apiPath, entity, responseType);
				} else {
					throw clientEx;
				}
			}
		}

		return success;
	}

	protected boolean shouldRetry(HttpClientErrorException clientEx, String action, String apiPath, String className) {
		boolean retry = false;

		switch (clientEx.getStatusCode()) {
		case FORBIDDEN:
			LOG.error("403 Forbidden: " + clientEx.getResponseBodyAsString());
			break;
		case UNAUTHORIZED:
			LOG.info("Unexpected 401, retrying...");
			if (mukRequestInterceptor instanceof DefaultRequestInterceptor) {
				final DefaultRequestInterceptor interceptor = (DefaultRequestInterceptor) mukRequestInterceptor;
				LOG.info("Auth ticket expires {}, now is {}",
						expireFormat.format(interceptor.getAuthTicketExpiration()), expireFormat.format(new Date()));
				interceptor.setAuthTicket(null);
				retry = true;
			}
			break;
		case NOT_FOUND:
			// 404 is okay for deletes
			if (!"delete".equals(action)) {
				logException(action, apiPath, className, clientEx);
				throw clientEx;
			}
			break;
		default:
			logException(action, apiPath, className, clientEx);
			throw clientEx;
		}

		return retry;
	}

	protected void logException(String action, String apiPath, String className, RestClientException e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Failed to {} {}.  Api: {}", action, className, apiPath, e);
			if (e instanceof HttpStatusCodeException) {
				LOG.debug("Server Message: {}", ((HttpStatusCodeException) e).getResponseBodyAsString());
			}
		}
	}

	protected RestTemplateBuilder getClient() {
		return restTemplateBuilder;
	}

	protected abstract String getEntityId(List<T> entity);

}
