/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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
package com.muk.services.api.impl;

import java.util.Collections;
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
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.services.api.DocService;
import com.muk.services.api.SecurityConfigurationService;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class CouchDbCmsService implements DocService {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbCmsService.class);

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Override
	public Map<String, Object> fetchDocById(String db, String docId, JsonQuery jq) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		final HttpEntity<Object> request = new HttpEntity<Object>(headers);
		final ResponseEntity<JsonNode> couchResponse = restTemplate.exchange(
				securityCfgService.getCouchDbUri() + "/" + db + "/" + docId, HttpMethod.GET, request, JsonNode.class);

		try {
			final List<JsonNode> nodes = jq.apply(couchResponse.getBody());
			response.put("json", nodes.get(0));
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

	@Override
	public Map<String, Object> addDoc(String db, Object doc) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		final HttpEntity<Object> request = new HttpEntity<Object>(doc, headers);
		final ResponseEntity<JsonNode> couchResponse = restTemplate
				.exchange(securityCfgService.getCouchDbUri() + "/" + db, HttpMethod.POST, request, JsonNode.class);

		response.put("json", couchResponse.getBody());

		return response;
	}

	@Override
	public Map<String, Object> updateDoc(String db, String docId, Object doc) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.setContentType(MediaType.APPLICATION_JSON);

		final HttpEntity<Object> request = new HttpEntity<Object>(doc, headers);
		final ResponseEntity<JsonNode> couchResponse = restTemplate.exchange(
				securityCfgService.getCouchDbUri() + "/" + db + "/" + docId, HttpMethod.PUT, request, JsonNode.class);

		response.put("json", couchResponse.getBody());

		return response;
	}
}
