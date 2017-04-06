package com.muk.services.commerce;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;

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

import com.muk.services.api.SubCrudService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractMozuSubService<T> implements SubCrudService<T> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMozuSubService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public boolean subUpsert(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception {
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
	public boolean subInsert(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception {
		boolean success = true;
		ResponseEntity<T> response = null;
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		try {
			response = restTemplateBuilder.postForEntity(apiPath, entity, responseType, urlVariableMap);
			success = response != null;
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "add", apiPath, getEntityId(entity), parentId)) {
				response = restTemplateBuilder.postForEntity(apiPath, entity, responseType, urlVariableMap);
				success = response != null;
			}
		} catch (final RestClientException e) {
			logException("add", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean subUpdate(String apiPath, String parentId, T entity) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		try {
			restTemplateBuilder.put(apiPath, entity, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "update", apiPath, getEntityId(entity), parentId)) {
				restTemplateBuilder.put(apiPath, entity, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("update", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return true;
	}

	@Override
	public T subRead(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));

		ResponseEntity<T> response = null;

		try {
			response = restTemplateBuilder.getForEntity(apiPath, responseType, urlVariableMap);
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "read", apiPath, getEntityId(entity), parentId)) {
				restTemplateBuilder.getForEntity(apiPath, responseType, urlVariableMap);
			}
		} catch (final RestClientException e) {
			logException("read", apiPath, getEntityId(entity), parentId, e);
			throw e;
		}

		return response.getBody();
	}

	@Override
	public boolean subDelete(String apiPath, String parentId, T entity) throws Exception {
		final MultiValueMap<String, String> urlVariableMap = new LinkedMultiValueMap<String, String>();
		urlVariableMap.put("id", Collections.singletonList(getEntityId(entity)));
		urlVariableMap.put("parentId", Collections.singletonList(parentId));
		urlVariableMap.putAll(getExtraParameters(entity));

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

	protected abstract String getEntityId(T entity);

	protected abstract MultiValueMap<String, String> getExtraParameters(T entity);

}
