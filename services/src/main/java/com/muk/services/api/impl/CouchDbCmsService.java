package com.muk.services.api.impl;

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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.services.api.CmsService;
import com.muk.services.api.SecurityConfigurationService;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class CouchDbCmsService implements CmsService {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbCmsService.class);

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Override
	public Map<String, Object> fetchDocById(String docId) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		final HttpEntity<Object> request = new HttpEntity<Object>(headers);
		final ResponseEntity<JsonNode> couchResponse = restTemplate.exchange(
				securityCfgService.getCouchDbUri() + "/cms/" + docId, HttpMethod.GET, request, JsonNode.class);

		try {
			final JsonQuery jq = JsonQuery.compile("{id: ._id, page: .sections}");
			final List<JsonNode> nodes = jq.apply(couchResponse.getBody());
			response.put("json", nodes.get(0));
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

}
