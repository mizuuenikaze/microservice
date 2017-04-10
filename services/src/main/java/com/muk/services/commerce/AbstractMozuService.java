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
import java.util.Collections;
import java.util.Date;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.muk.services.api.CrudService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractMozuService<T> implements CrudService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMozuService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();
	private static Pattern mukErrorCode = Pattern.compile("errorCode\":\"(\\w+)");

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public boolean insert(String apiPath, T entity, Class<T> responseType) throws Exception {
		boolean success = true;
		ResponseEntity<T> response = null;
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			response = restTemplateBuilder.postForEntity(apiPath, entity, responseType, urlVariableMap);
			success = response != null;
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "add", apiPath, responseType.getName())) {
				response = restTemplateBuilder.postForEntity(apiPath, entity, responseType, urlVariableMap);
				success = response != null;
			}
		} catch (final RestClientException e) {
			logException("add", apiPath, responseType.getName(), e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean update(String apiPath, T entity) throws Exception {
		final boolean success = true;
		final String entityId = getEntityId(entity);
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			if (entityId != null) {
				urlVariableMap.put("id", Collections.singletonList(entityId));
			}

			restTemplateBuilder.put(apiPath, entity, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "update", apiPath, entityId)) {
				restTemplateBuilder.put(apiPath, entity, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("update", apiPath, entityId, e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean delete(String apiPath, T entity) throws Exception {
		final String entityId = getEntityId(entity);
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		try {
			if (entityId != null) {
				urlVariableMap.put("id", Collections.singletonList(entityId));
			}

			restTemplateBuilder.delete(apiPath, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "delete", apiPath, entityId)) {
				restTemplateBuilder.delete(apiPath, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("delete", apiPath, entityId, e);
			throw e;
		}

		return true;
	}

	@Override
	public T read(String apiPath, T entityTemplate, MultiValueMap<String, String> parameters, Class<T> responseType)
			throws Exception {
		ResponseEntity<T> response = null;
		final String entityId = getEntityId(entityTemplate);
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		if (parameters != null) {
			urlVariableMap.putAll(parameters);
		}

		if (entityId != null) {
			urlVariableMap.put("id", Collections.singletonList(entityId));
		}

		try {
			response = restTemplateBuilder.getForEntity(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "get", apiPath, responseType.getName())) {
				response = restTemplateBuilder.getForEntity(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("get", apiPath, responseType.getName(), e);
			throw e;
		}

		if (response != null) {
			return response.getBody();
		}

		return null;
	}

	@Override
	public boolean upsert(String apiPath, T entity, Class<T> responseType) throws Exception {
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

	protected abstract String getEntityId(T entity);

}
