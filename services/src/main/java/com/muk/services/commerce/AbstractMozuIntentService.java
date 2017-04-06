package com.muk.services.commerce;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import javax.inject.Inject;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpRequestInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;

import com.muk.services.api.IntentService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.web.client.DefaultRequestInterceptor;

public abstract class AbstractMozuIntentService<T, U> implements IntentService<T, U> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractMozuIntentService.class);
	private static DateFormat expireFormat = SimpleDateFormat.getInstance();

	@Inject
	@Qualifier("restTemplateBuilder")
	private RestTemplateBuilder restTemplateBuilder;

	@Inject
	@Qualifier("streamingRestTemplateBuilder")
	private RestTemplateBuilder streamingRestTemplateBuilder;

	@Inject
	@Qualifier("mukRequestInterceptor")
	private HttpRequestInterceptor mukRequestInterceptor;

	@Override
	public boolean perform(String apiPath, T request, Class<U> responseType) throws Exception {
		boolean success = true;
		ResponseEntity<U> response = null;

		try {
			response = restTemplateBuilder.postForEntity(apiPath, request, responseType, buildVariableMap(request));
			success = response != null;
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "operation", apiPath, responseType.getName())) {
				response = restTemplateBuilder.postForEntity(apiPath, request, responseType, buildVariableMap(request));
				success = response != null;
			}
		} catch (final RestClientException e) {
			logException("operation ", apiPath, responseType.getName(), e);
			throw e;
		}

		return success;
	}

	@Override
	public boolean performStreaming(String apiPath, T entity, Class<U> responseType, boolean download)
			throws Exception {
		boolean success = true;
		ResponseEntity<U> response = null;

		try {
			if (download) {
				final HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

				success = Boolean.TRUE.equals(streamingRestTemplateBuilder.execute(apiPath, HttpMethod.GET,
						new StreamRequestCallback(headers), new StreamExtractor(getOutputLocation(entity)),
						buildVariableMap(entity)));
			} else {
				response = streamingRestTemplateBuilder.exchange(apiPath, HttpMethod.PUT,
						new HttpEntity<Resource>(buildResource(entity)), responseType, buildVariableMap(entity));
				success = response != null;
			}
		} catch (final HttpClientErrorException clientEx) {
			if (shouldRetry(clientEx, "operation", apiPath, responseType.getName())) {
				if (download) {
					final HttpHeaders headers = new HttpHeaders();
					headers.setAccept(Arrays.asList(MediaType.APPLICATION_OCTET_STREAM));

					success = Boolean.TRUE.equals(streamingRestTemplateBuilder.execute(apiPath, HttpMethod.GET,
							new StreamRequestCallback(headers), new StreamExtractor(getOutputLocation(entity)),
							buildVariableMap(entity)));
				} else {
					response = streamingRestTemplateBuilder.exchange(apiPath, HttpMethod.PUT,
							new HttpEntity<Resource>(buildResource(entity)), responseType, buildVariableMap(entity));
					success = response != null;
				}
			}
		} catch (final RestClientException e) {
			logException("operation ", apiPath, responseType.getName(), e);
			throw e;
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

	protected abstract MultiValueMap<String, String> buildVariableMap(T entity);

	protected abstract Resource buildResource(T entity);

	protected abstract String getOutputLocation(T entity);

	private class StreamExtractor implements ResponseExtractor<Boolean> {
		private OutputStream ostream;

		public StreamExtractor(String filePath) {
			try {
				ostream = new FileOutputStream(new File(filePath));
			} catch (final FileNotFoundException e) {
				try {
					ostream = new FileOutputStream(File.createTempFile("download", ""));
				} catch (final Exception ex) {
					// nop
				}
			}
		}

		@Override
		public Boolean extractData(ClientHttpResponse response) throws IOException {

			Boolean result;

			if (response.getStatusCode() == HttpStatus.OK) {
				result = IOUtils.copy(response.getBody(), ostream) > 0;
			} else {
				result = Boolean.FALSE;
				LOG.error("Failed to download file. Status: {}", response.getStatusCode().value());
			}

			response.close();
			ostream.flush();
			ostream.close();

			return result;
		}
	}

	private class StreamRequestCallback implements RequestCallback {
		private final HttpHeaders headers;

		public StreamRequestCallback(HttpHeaders headers) {
			this.headers = headers;
		}

		@Override
		public void doWithRequest(ClientHttpRequest request) throws IOException {
			final HttpHeaders reqHeaders = request.getHeaders();
			reqHeaders.putAll(headers);
		}
	}
}
