package com.muk.services.api.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.PaymentService;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.exchange.ServiceConstants;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class PayPalPaymentService implements PaymentService {
	private static final Logger LOG = LoggerFactory.getLogger(PayPalPaymentService.class);

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
		final ResponseEntity<JsonNode> paymentResponse = send("/payments/payment", payload);

		try {
			final JsonQuery jq = JsonQuery.compile("{paymentId: .id, state: .state}");
			final List<JsonNode> nodes = jq.apply(paymentResponse.getBody());
			response.put("json", nodes.get(0));
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

	@Override
	public Map<String, Object> commitPayment(String paymentId, String payerId) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final ObjectNode payload = JsonNodeFactory.instance.objectNode();
		payload.put("payer_id", payerId);

		final ResponseEntity<JsonNode> paymentResponse = send("/payments/payment/" + paymentId + "/execute", payload);

		if (!"approved".equals(paymentResponse.getBody().get("state").asText())) {
			response.put("error", "Unexpected status: " + paymentResponse.getBody().get("state").asText());
		} else {
			response.put("success", true);
		}

		return response;
	}

	private String getTokenHeader() {
		final Cache cache = cacheManager.getCache(ServiceConstants.CacheNames.paymentApiTokenCache);
		final String token = "paypal";
		ValueWrapper valueWrapper = cache.get(token);
		String cachedHeader = StringUtils.EMPTY;

		if (valueWrapper == null || valueWrapper.get() == null) {
			try {
				final String value = securityCfgService.getPayPalClientId() + ":"
						+ keystoreService.getPBEKey(securityCfgService.getPayPalClientId());

				final HttpHeaders headers = new HttpHeaders();
				headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON_UTF8));
				headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
				headers.add(HttpHeaders.AUTHORIZATION,
						"Basic " + nonceService.encode(value.getBytes(StandardCharsets.UTF_8)));

				final MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
				body.add("grant_type", "client_credentials");

				final HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(
						body, headers);

				final ResponseEntity<JsonNode> response = restTemplate
						.postForEntity(securityCfgService.getPayPalUri() + "/oauth2/token", request, JsonNode.class);

				cache.put(token, response.getBody().get("access_token").asText());
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
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add(HttpHeaders.AUTHORIZATION, getTokenHeader());

		return restTemplate.postForEntity(securityCfgService.getPayPalUri() + path,
				new HttpEntity<JsonNode>(payload, headers), JsonNode.class);
	}

}
