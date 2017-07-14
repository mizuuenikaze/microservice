package com.muk.services.api.impl;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.PaymentService;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.exchange.ServiceConstants;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class StripePaymentService implements PaymentService {
	private static final Logger LOG = LoggerFactory.getLogger(StripePaymentService.class);

	@Inject
	private CacheManager cacheManager;

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("hashService")
	private NonceService nonceService;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Override
	public Map<String, Object> startPayment(JsonNode payload) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final ResponseEntity<JsonNode> paymentResponse = send("/charges", payload);

		try {
			final JsonQuery jqError = JsonQuery.compile(".failure_code, .failure_message");
			final List<JsonNode> errorNodes = jqError.apply(paymentResponse.getBody());

			if (errorNodes.get(0).isNull()) {
				final JsonQuery jq = JsonQuery
						.compile("{paymentId: .id, state: if .captured then \"created\" else \"not captured\" end }");
				final List<JsonNode> nodes = jq.apply(paymentResponse.getBody());
				response.put("json", nodes.get(0));
			} else {
				response.put("error", errorNodes.get(0).asText() + ": " + errorNodes.get(1).asText());
			}
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

	@Override
	public Map<String, Object> commitPayment(String paymentId, String payerId) {
		throw new UnsupportedOperationException();
	}

	private String getTokenHeader() {
		final Cache cache = cacheManager.getCache(ServiceConstants.CacheNames.paymentApiTokenCache);
		final String token = "stripe";
		ValueWrapper valueWrapper = cache.get(token);
		String cachedHeader = StringUtils.EMPTY;

		if (valueWrapper == null || valueWrapper.get() == null) {
			try {
				final String value = keystoreService.getPBEKey(securityCfgService.getStripeClientId());

				cache.put(token, value);
				valueWrapper = cache.get(token);
				cachedHeader = (String) valueWrapper.get();
			} catch (final IOException ioEx) {
				LOG.error("Failed read keystore", ioEx);
				cachedHeader = StringUtils.EMPTY;
			} catch (final GeneralSecurityException secEx) {
				LOG.error("Failed to get key", secEx);
				cachedHeader = StringUtils.EMPTY;
			}
		} else {
			cachedHeader = (String) valueWrapper.get();
		}

		return "Bearer " + cachedHeader;
	}

	private ResponseEntity<JsonNode> send(String path, JsonNode payload) {
		final HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.add(HttpHeaders.AUTHORIZATION, getTokenHeader());

		final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		final Iterator<Entry<String, JsonNode>> nodes = payload.fields();

		while (nodes.hasNext()) {
			final Map.Entry<String, JsonNode> entry = nodes.next();

			if (entry.getValue().isObject()) {
				final String key = entry.getKey();
				final Iterator<Entry<String, JsonNode>> metadataNodes = entry.getValue().fields();

				while (metadataNodes.hasNext()) {
					final Map.Entry<String, JsonNode> element = metadataNodes.next();
					body.add(key + "[\"" + element.getKey() + "\"]", element.getValue().asText());
				}
			} else {
				body.add(entry.getKey(), entry.getValue().asText());
			}
		}

		return restTemplate.postForEntity(securityCfgService.getStripeUri() + path,
				new HttpEntity<MultiValueMap<String, String>>(body, headers), JsonNode.class);
	}

}
