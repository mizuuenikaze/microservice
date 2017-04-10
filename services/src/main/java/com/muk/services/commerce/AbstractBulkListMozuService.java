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
import java.util.Collection;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;

import com.muk.services.api.BulkReadListService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractBulkListMozuService<T, TWRAPPER> implements BulkReadListService<T, TWRAPPER> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractBulkListMozuService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public List<T> getAll(String apiPath, Integer startIndex, Integer pageSize, String sortBy, String filter,
			String responseFields, ParameterizedTypeReference<List<T>> responseType) throws Exception {
		List<T> allResults = null;
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		if (startIndex != null) {
			urlVariableMap.put("startIndex", Collections.singletonList(String.valueOf(startIndex)));
		}
		if (pageSize != null) {
			urlVariableMap.put("pageSize", Collections.singletonList(String.valueOf(pageSize)));
		}
		if (StringUtils.isNotBlank(filter)) {
			urlVariableMap.put("filter", Collections.singletonList(filter));
		}
		if (StringUtils.isNotBlank(sortBy)) {
			urlVariableMap.put("sortBy", Collections.singletonList(sortBy));
		}
		if (StringUtils.isNotBlank(responseFields)) {
			urlVariableMap.put("responseFields", Collections.singletonList(responseFields));
		}

		try {
			allResults = findByInternal(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, filter, apiPath, responseType.getClass().getName())) {
				allResults = findByInternal(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException(filter, apiPath, responseType.getClass().getName(), e);
			throw e;
		}

		return allResults;
	}

	@Override
	public Collection<T> getAll(String apiPath, ParameterizedTypeReference<List<T>> responseType) throws Exception {
		Integer startIndex = 0;
		final Integer pageSize = 200;

		final List<T> all = getNewList();
		List<T> collection = null;

		do {
			collection = getAll(apiPath, startIndex, pageSize, StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
					responseType);

			if (collection != null && !collection.isEmpty()) {
				all.addAll(collection);
			}

			startIndex += pageSize;
		} while (collection != null && startIndex < collection.size());

		return all;
	}

	@Override
	public List<T> findBy(String apiPath, String filter, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		List<T> allResults = null;

		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("startIndex", Collections.singletonList("0"));
		urlVariableMap.put("pageSize", Collections.singletonList("2"));
		urlVariableMap.put("filter", Collections.singletonList(filter));
		// urlVariableMap.put("sortBy", null);
		// urlVariableMap.put("responseFilter", null);

		try {
			allResults = findByInternal(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, filter, apiPath, responseType.getClass().getName())) {
				allResults = findByInternal(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException(filter, apiPath, responseType.getClass().getName(), e);
			throw e;
		}

		return allResults;
	}

	@Override
	public List<T> getSubAll(String apiPath, String parentId, Integer startIndex, Integer pageSize, String sortBy,
			String filter, String responseFields, ParameterizedTypeReference<List<T>> responseType) throws Exception {
		List<T> allResults = null;
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();

		if (startIndex != null) {
			urlVariableMap.put("startIndex", Collections.singletonList(String.valueOf(startIndex)));
		}
		if (pageSize != null) {
			urlVariableMap.put("pageSize", Collections.singletonList(String.valueOf(pageSize)));
		}
		if (StringUtils.isNotBlank(filter)) {
			urlVariableMap.put("filter", Collections.singletonList(filter));
		}
		if (StringUtils.isNotBlank(sortBy)) {
			urlVariableMap.put("sortBy", Collections.singletonList(sortBy));
		}
		if (StringUtils.isNotBlank(responseFields)) {
			urlVariableMap.put("responseFilter", Collections.singletonList(responseFields));
		}

		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		try {
			allResults = findByInternal(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, filter, apiPath, responseType.getClass().getName(), parentId)) {
				allResults = findByInternal(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logSubException(filter, apiPath, responseType.getClass().getName(), parentId, e);
			throw e;
		}

		return allResults;
	}

	@Override
	public Collection<T> getSubAll(String apiPath, String parentId, ParameterizedTypeReference<List<T>> responseType)
			throws Exception {
		Integer startIndex = 0;
		final Integer pageSize = 200;

		final Collection<T> all = getNewList();
		List<T> collection = null;

		do {
			collection = getSubAll(apiPath, parentId, startIndex, pageSize, StringUtils.EMPTY, StringUtils.EMPTY,
					StringUtils.EMPTY, responseType);

			if (collection != null && !collection.isEmpty()) {
				all.addAll(collection);
			}

			startIndex += pageSize;
		} while (collection != null && startIndex < collection.size());

		return all;
	}

	@Override
	public List<T> findSubBy(String apiPath, String parentId, String filter,
			ParameterizedTypeReference<List<T>> responseType) throws Exception {
		List<T> allResults = null;

		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("startIndex", Collections.singletonList("0"));
		urlVariableMap.put("pageSize", Collections.singletonList("2"));
		urlVariableMap.put("filter", Collections.singletonList(filter));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));
		// urlVariableMap.put("sortBy", null);
		// urlVariableMap.put("responseFilter", null);

		try {
			allResults = findByInternal(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, filter, apiPath, responseType.getClass().getName(), parentId)) {
				allResults = findByInternal(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logSubException(filter, apiPath, responseType.getClass().getName(), parentId, e);
			throw e;
		}

		return allResults;
	}

	protected List<T> findByInternal(String apiPath, ParameterizedTypeReference<List<T>> responseType,
			MultiValueMap<String, String> urlVariableMap) throws Exception {
		final ResponseEntity<List<T>> response = restTemplateBuilder.exchange(apiPath, HttpMethod.GET, null,
				responseType, urlVariableMap);

		if (response != null) {
			return response.getBody();
		}

		return null;
	}

	protected RestTemplateBuilder getClient() {
		return restTemplateBuilder;
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
		case INTERNAL_SERVER_ERROR:
			// Read timeouts -- can't do anything about that.
			if (!clientEx.getMessage().contains("Read timed out")) {
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
		case BAD_REQUEST:
			if (clientEx.getResponseBodyAsString().indexOf("errorCode\":\"ITEM_NOT_FOUND") != -1) {
				logSubException(action, apiPath, className, parentId, clientEx);
			}
			break;
		default:
			logSubException(action, apiPath, className, parentId, clientEx);
			throw clientEx;
		}

		return retry;
	}

	protected void logException(String filter, String apiPath, String className, RestClientException e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Failed to find {}.  Filter: {} Api: {}", className, filter, apiPath, e);
		}
	}

	protected void logSubException(String filter, String apiPath, String className, String parentId,
			RestClientException e) {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Failed to find {}.  Filter: {} Api: {} Parent: {}", className, filter, apiPath, parentId, e);
			if (e instanceof HttpStatusCodeException) {
				LOG.debug("Server Message: {}", ((HttpStatusCodeException) e).getResponseBodyAsString());
			}
		}
	}

	protected abstract List<T> getNewList();

}
