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
import java.util.List;

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

import com.muk.services.api.SubCrudListService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractListMozuSubService<T> implements SubCrudListService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractListMozuSubService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public boolean subUpsert(String apiPath, String parentId, List<T> entity,
			ParameterizedTypeReference<List<T>> responseType) throws Exception {
		boolean success = true;

		final String entityId = getEntityId(entity);
		if (StringUtils.isBlank(entityId)) {
			success = subInsert(apiPath, parentId, entity, responseType);
		} else {
			try {
				success = subUpdate(apiPath, parentId, entity);
			} catch (final HttpClientErrorException clientEx) {
				if (clientEx.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
					success = subInsert(apiPath, parentId, entity, responseType);
				} else {
					throw clientEx;
				}
			}
		}

		return success;
	}

	@Override
	public boolean subInsert(String apiPath, String parentId, List<T> entity,
			ParameterizedTypeReference<List<T>> responseType) throws Exception {
		boolean success = true;
		ResponseEntity<Object> response = null;
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		try {
			response = restTemplateBuilder.exchange(apiPath, HttpMethod.POST, httpEntity, Object.class, urlVariableMap);
			success = response != null;
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "add", apiPath, getEntityId(entity), parentId)) {
				response = restTemplateBuilder.exchange(apiPath, HttpMethod.POST, httpEntity, Object.class,
						urlVariableMap);
				success = response != null;
			}
		} catch (final RestClientException e) {
			logException("add", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean subUpdate(String apiPath, String parentId, List<T> entity) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());

		try {
			restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, Object.class, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "update", apiPath, getEntityId(entity), parentId)) {
				restTemplateBuilder.exchange(apiPath, HttpMethod.PUT, httpEntity, Object.class, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("update", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return true;
	}

	@Override
	public List<T> subRead(String apiPath, String parentId, List<T> entity,
			ParameterizedTypeReference<List<T>> responseType) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));
		final HttpEntity<List<T>> httpEntity = new HttpEntity<List<T>>(entity, new HttpHeaders());

		ResponseEntity<List<T>> response = null;

		try {
			response = restTemplateBuilder.exchange(apiPath, HttpMethod.GET, httpEntity, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "read", apiPath, getEntityId(entity), parentId)) {
				response = restTemplateBuilder.exchange(apiPath, HttpMethod.GET, httpEntity, responseType,
						urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("read", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return response.getBody();
	}

	@Override
	public boolean subDelete(String apiPath, String parentId, List<T> entity) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		try {
			restTemplateBuilder.delete(apiPath, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "delete", apiPath, getEntityId(entity), parentId)) {
				restTemplateBuilder.delete(apiPath, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("delete", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return true;
	}

	protected boolean shouldRetry(HttpClientErrorException clientEx, String action, String apiPath, String className,
			String parentId) {
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
				logException(action, apiPath, className, parentId, clientEx);
				throw clientEx;
			}
			break;
		default:
			logException(action, apiPath, className, parentId, clientEx);
			throw clientEx;
		}

		return retry;
	}

	protected void logException(String action, String apiPath, String className, String parentId,
			RestClientException e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Failed to {} {}.  Api: {} Parent: {}", action, className, apiPath, parentId, e);
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
