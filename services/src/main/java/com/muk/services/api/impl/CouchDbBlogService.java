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

import java.io.IOException;
import java.util.ArrayList;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.services.api.BlogService;
import com.muk.services.api.SecurityConfigurationService;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.Scope;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class CouchDbBlogService implements BlogService {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbBlogService.class);
	private static final String PAGING_MODE_LINKEDLIST = "linkedlist";
	private static final String PAGING_MODE_OFFSET = "offset";
	private static final String JQ_ROW_KEY = "rows";
	private static final String JQ_LAST_ROW = "(.rows | length) - 1";
	private static JsonQuery JQ_ROW_QUERY;

	@Inject
	@Qualifier("jsonObjectMapper")
	private ObjectMapper objectMapper;

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	static {
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("if (.rows | length) < $vars.limit").append(" then {rows: .rows} else {rows: .rows[0:")
				.append(JQ_LAST_ROW).append("]} end");
		try {
			JQ_ROW_QUERY = JsonQuery.compile(queryBuilder.toString());
		} catch (final JsonQueryException jqEx) {
			LOG.error("failed early jq compile", jqEx);
			JQ_ROW_QUERY = null;
		}
	}

	@Override
	public Map<String, Object> fetchPagedSummaryView(String mode, Long startKey, String startKeyDocId, Long limit,
			Integer pageLimit, Long offset) {
		final Map<String, Object> response = new HashMap<String, Object>();

		//rest request setup
		final HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

		final HttpEntity<Object> viewRequest = new HttpEntity<Object>(headers);

		final UriComponentsBuilder nextBuilder = UriComponentsBuilder
				.fromUriString(securityCfgService.getCouchDbUri() + "/blog/_design/ddoc/_view/entries");
		nextBuilder.queryParam("limit", limit);

		final UriComponentsBuilder prevBuilder = nextBuilder.cloneBuilder();

		//jq variables
		final Scope varScope = new Scope(null);
		varScope.loadFunctions(Thread.currentThread().getContextClassLoader());
		try {
			varScope.setValue("vars", objectMapper.readTree("{\"limit\": " + limit + "}"));
		} catch (final IOException ioEx) {
			LOG.error("Failed to set jq scope variables.", ioEx);
		}

		final ObjectNode combinedNode = objectMapper.createObjectNode();
		final List<JsonNode> nextNodes = new ArrayList<JsonNode>(pageLimit);
		final List<JsonNode> nextPages = new ArrayList<JsonNode>(pageLimit);
		final List<JsonNode> prevPages = new ArrayList<JsonNode>(pageLimit);

		try {
			if (PAGING_MODE_LINKEDLIST.equals(mode)) {
				if (startKey != null && startKeyDocId != null) {
					nextBuilder.queryParam("startkey", startKey);
					nextBuilder.queryParam("startkey_docid", startKeyDocId);
				}

				// get the next batch of possible paging results
				nextNodes.addAll(getPagedRows(nextPages, nextBuilder.build().toUriString(), viewRequest, 0,
						pageLimit.intValue(), limit, varScope));

				if (!nextPages.isEmpty()) {
					combinedNode.putArray("nextpages").addAll(nextPages);
				}

				combinedNode.setAll((ObjectNode) nextNodes.get(0));

				if (setPreviousPageStart(prevBuilder, nextNodes.get(0).get(JQ_ROW_KEY).get(0), viewRequest, varScope)) {
					// get the previous batch of possible paging results
					prevBuilder.queryParam("descending", true);

					getPagedRows(prevPages, prevBuilder.build().toUriString(), viewRequest, 0, pageLimit.intValue(),
							limit - 1, varScope);
				}

				if (!prevPages.isEmpty()) {
					combinedNode.putArray("prevpages").addAll(prevPages);
				}
			} else if (PAGING_MODE_OFFSET.equals(mode)) {
				//TODO Consider the offset mode of paging...
			}

			response.put("json", combinedNode);

		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

	/**
	 * Builds a list of view results to get the rows to display and to precompute a number of pagination links.
	 *
	 *
	 **/
	private List<JsonNode> getPagedRows(List<JsonNode> pageLinks, String uri, HttpEntity<Object> request, int step,
			int sentinel, Long limit, Scope varScope) throws JsonQueryException {

		if (sentinel == 0) {
			return Collections.emptyList();
		}

		final boolean isFirst = (step == 0);
		if (!isFirst) {
			uri += "&skip=" + (step * limit);
		}

		final ResponseEntity<JsonNode> couchResponse = restTemplate.exchange(uri, HttpMethod.GET, request,
				JsonNode.class);

		final JsonNode responseBody = couchResponse.getBody();

		if (responseBody.hasNonNull(JQ_ROW_KEY) && responseBody.get(JQ_ROW_KEY).isArray()
				&& responseBody.get(JQ_ROW_KEY).size() != 0) {

			if (limit <= responseBody.get(JQ_ROW_KEY).size()) {

				final JsonNode pageKeyNode = buildPaginationKey(responseBody.get(JQ_ROW_KEY));

				if (pageKeyNode != null) {
					try {
						pageLinks.add(pageKeyNode);
					} catch (final UnsupportedOperationException supOpEx) {
						// called with immutable list so the results are not interesting
					}
				}

				//get the next set of results
				getPagedRows(pageLinks, uri, request, ++step, --sentinel, limit, varScope);
			}

			if (isFirst) {
				return JQ_ROW_QUERY.apply(varScope, responseBody);
			}
		}

		return Collections.emptyList();
	}

	private JsonNode buildPaginationKey(JsonNode rows) {
		if (!rows.isArray() || rows.size() == 0) {
			return null;
		}

		final ObjectNode pageNode = objectMapper.createObjectNode();
		final JsonNode row = rows.get(rows.size() - 1);

		pageNode.set("startkey", row.get("key"));
		pageNode.set("startkey_docid", row.get("id"));

		return pageNode;
	}

	private boolean setPreviousPageStart(UriComponentsBuilder uriBuilder, JsonNode firstResult,
			HttpEntity<Object> viewRequest, Scope varScope) throws JsonQueryException {
		boolean success = false;
		final UriComponentsBuilder clonedBuilder = uriBuilder.cloneBuilder();
		clonedBuilder.queryParam("descending", true);
		clonedBuilder.queryParam("startkey", firstResult.get("key"));
		clonedBuilder.queryParam("startkey_docid", firstResult.get("id"));
		clonedBuilder.replaceQueryParam("limit", "3");

		final List<JsonNode> previousNodes = new ArrayList<JsonNode>();

		// previousNodes.addAll(getPagedRows(Collections.emptyList(), clonedBuilder.build().toUriString(), viewRequest, 0, 1, 3l, varScope));

		// There should be either one or zero rows returned and the first row is the one needed as the new startkey.

		if (!previousNodes.isEmpty() && previousNodes.get(0).get(JQ_ROW_KEY).size() == 2) {
			success = true;

			final JsonNode previousResults = previousNodes.get(0).get(JQ_ROW_KEY);

			uriBuilder.queryParam("startkey", previousResults.get(1).get("key"));
			uriBuilder.queryParam("startkey_docid", previousResults.get(1).get("id"));
		}

		/* short circuit -- use the first row */
		uriBuilder.queryParam("startkey", firstResult.get("key"));
		uriBuilder.queryParam("startkey_docid", firstResult.get("id"));

		return true;
		//return success;

	}
}
