package com.muk.services.api.builder.impl;

import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RequestCallback;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.services.api.MozuConfigurationService;
import com.muk.services.api.builder.RestTemplateBuilder;

public class RestTemplateBuilderImpl implements RestTemplateBuilder {
	private static final Logger LOG = LoggerFactory.getLogger(RestTemplateBuilderImpl.class);
	private static final Pattern domainPat = Pattern.compile("^\\{(base)|(tenant)|(pci)\\}");

	private MozuConfigurationService mukConfigurationService;
	private RestTemplate restTemplate;


	public void setMozuConfigurationService(MozuConfigurationService mukConfigurationService) {
		this.mukConfigurationService = mukConfigurationService;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	private URI normalizeUri(String url, Object uriVariables) {
		URI uri = null;
		String domainType = null;
		MultiValueMap<String, String> uriVariableMap = new LinkedMultiValueMap<String, String>(1);
		UriComponentsBuilder uriBuilder = null;

		if (uriVariables instanceof MultiValueMap) {
			uriVariableMap = (MultiValueMap<String, String>) uriVariables;
		}

		try {
			final Matcher matcher = domainPat.matcher(url);
			if (matcher.find()) {
				for (int i = 1; i < 4; i++) {
					domainType = matcher.group(i);
					if (domainType != null) {
						break;
					}
				}

				switch (domainType) {
				case "base":
					url = url.replace("{base}", mukConfigurationService.getBaseUrl());
					break;
				case "pci":
					url = url.replace("{pci}", mukConfigurationService.getPciUrl());
					break;
				}
			}

			// append variables to uri
			uriBuilder = UriComponentsBuilder.fromUriString(url);
			final Map<String, String> replaceVars = new HashMap<String, String>();

			if (uriVariableMap.get("parentId") != null) {
				replaceVars.put("parentId", uriVariableMap.remove("parentId").get(0));
			}

			if (uriVariableMap.get("id") != null) {
				if (url.indexOf("{id}") != -1) {
					replaceVars.put("id", uriVariableMap.remove("id").get(0));
				} else {
					uriBuilder.pathSegment("{id}");
					replaceVars.put("id", uriVariableMap.remove("id").get(0));
				}
			}

			uriBuilder.queryParams(uriVariableMap);

			uri = (uriBuilder.buildAndExpand(replaceVars).encode().toUri());

			if (StringUtils.isBlank(uri.getScheme())) {
				final URI base = new URI(mukConfigurationService.getBaseUrl());
				uri = base.resolve(uri);
			}
		} catch (final URISyntaxException uriSyntaxEx) {
			uri = null;
		}

		return uri;
	}

	@Override
	public <T> T getForObject(String url, Class<T> responseType, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T getForObject(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		try {
			return restTemplate.getForObject(normalizeUri(url, uriVariables), responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> T getForObject(URI url, Class<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		throw new UnsupportedOperationException();

	}

	@Override
	public <T> ResponseEntity<T> getForEntity(String url, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		try {
			return restTemplate.getForEntity(normalizeUri(url, uriVariables), responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> ResponseEntity<T> getForEntity(URI url, Class<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpHeaders headForHeaders(String url, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public HttpHeaders headForHeaders(String url, Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.headForHeaders(url, uriVariables);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public HttpHeaders headForHeaders(URI url) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public URI postForLocation(String url, Object request, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public URI postForLocation(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.postForLocation(url, request, uriVariables);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public URI postForLocation(URI url, Object request) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T postForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		try {
			return restTemplate.postForObject(normalizeUri(url, uriVariables), request, responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> T postForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType,
			Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(String url, Object request, Class<T> responseType,
			Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.postForEntity(normalizeUri(url, uriVariables), request, responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> ResponseEntity<T> postForEntity(URI url, Object request, Class<T> responseType)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(String url, Object request, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void put(String url, Object request, Map<String, ?> uriVariables) throws RestClientException {
		try {
			restTemplate.put(normalizeUri(url, uriVariables), request);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}
	}

	@Override
	public void put(URI url, Object request) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String url, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(String url, Map<String, ?> uriVariables) throws RestClientException {
		try {
			restTemplate.delete(normalizeUri(url, uriVariables));
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}
	}

	@Override
	public void delete(URI url) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<HttpMethod> optionsForAllow(String url, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<HttpMethod> optionsForAllow(String url, Map<String, ?> uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Set<HttpMethod> optionsForAllow(URI url) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.exchange(normalizeUri(url, uriVariables), method, requestEntity, responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			Class<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType, Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.exchange(normalizeUri(url, uriVariables), method, requestEntity, responseType);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> ResponseEntity<T> exchange(URI url, HttpMethod method, HttpEntity<?> requestEntity,
			ParameterizedTypeReference<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, Class<T> responseType)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> ResponseEntity<T> exchange(RequestEntity<?> requestEntity, ParameterizedTypeReference<T> responseType)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Object... uriVariables) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T execute(String url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor, Map<String, ?> uriVariables) throws RestClientException {
		try {
			return restTemplate.execute(normalizeUri(url, uriVariables), method, requestCallback, responseExtractor);
		} catch (final RestClientException e) {
			logUnrecoveralUnfatalError(e);
		}

		return null;
	}

	@Override
	public <T> T execute(URI url, HttpMethod method, RequestCallback requestCallback,
			ResponseExtractor<T> responseExtractor) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T patchForObject(String url, Object request, Class<T> responseType, Object... uriVariables)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T patchForObject(String url, Object request, Class<T> responseType, Map<String, ?> uriVariables)
			throws RestClientException {
		throw new UnsupportedOperationException();
	}

	@Override
	public <T> T patchForObject(URI url, Object request, Class<T> responseType) throws RestClientException {
		throw new UnsupportedOperationException();
	}

	private void logUnrecoveralUnfatalError(RestClientException e) {
		if (e.getCause() instanceof SocketTimeoutException) {
			if (e.getCause().getMessage().indexOf("Read timed out") != -1) {
				LOG.warn("Client socket read timeout on muk call.");
				return;
			}
		}

		throw e;
	}
}
