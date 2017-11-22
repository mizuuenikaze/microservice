package com.muk.services.api.impl;

import java.util.ArrayList;
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
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.services.api.BlogService;
import com.muk.services.api.SecurityConfigurationService;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class CouchDbBlogService implements BlogService {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbBlogService.class);
	private static final String PAGING_MODE_LINKEDLIST = "linkedlist";
	private static final String PAGING_MODE_OFFSET = "offset";
	private static final String JQ_LAST_ROW = "(.rows | length) - 1";

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService securityCfgService;

	@Override
	public Map<String, Object> fetchPagedSummaryView(String mode, Long startKey, String startKeyDocId, Long limit,
			Long offset) {
		final Map<String, Object> response = new HashMap<String, Object>();
		final MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

		final UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(securityCfgService.getCouchDbUri() + "/blog/_design/ddoc/_view/entries");
		uriBuilder.queryParam("limit", limit);
		UriComponentsBuilder prevBuilder = null;
		StringBuilder queryBuilder = new StringBuilder();
		final List<JsonQuery> jqQueries = new ArrayList<JsonQuery>();
		List<JsonNode> nextNodes = new ArrayList<JsonNode>(1);
		List<JsonNode> prevNodes = new ArrayList<JsonNode>(1);

		try {
			if (PAGING_MODE_LINKEDLIST.equals(mode)) {
				if (startKey != null && startKeyDocId != null) {
					uriBuilder.queryParam("startkey", startKey);
					uriBuilder.queryParam("startkey_docid", startKeyDocId);
				}

				prevBuilder = uriBuilder.cloneBuilder();
				prevBuilder.queryParam("descending", true);

				// build array of next startkeys
				jqQueries.add(JsonQuery.compile(buildStartkeys(true, limit)));

				queryBuilder = new StringBuilder();
				queryBuilder.append("{rows: if (.rows | length) < ").append(limit)
						.append(" then [.rows[]] else [.rows[0:").append(JQ_LAST_ROW).append("]] end}");
				jqQueries.add(JsonQuery.compile(queryBuilder.toString()));

				nextNodes = getPagedRows(uriBuilder.build().toUriString(), new HttpEntity<Object>(headers), jqQueries);

				// build array of previous startkeys
				jqQueries.clear();
				jqQueries.add(JsonQuery.compile(buildStartkeys(false, limit)));
				prevNodes = getPagedRows(prevBuilder.build().toUriString(), new HttpEntity<Object>(headers), jqQueries);

			} else if (PAGING_MODE_OFFSET.equals(mode)) {
				if (offset != null) {
					uriBuilder.queryParam("skip", offset);
				} else {
					uriBuilder.queryParam("skip", 0);
				}

				// build next offset
				queryBuilder.append("{offset:.offset, rows:.rows[]}");
				jqQueries.add(JsonQuery.compile(queryBuilder.toString()));

				nextNodes = getPagedRows(uriBuilder.build().toUriString(), new HttpEntity<Object>(headers), jqQueries);
			}

			// combine json nodes
			final ObjectNode combinedNode = JsonNodeFactory.instance.objectNode();

			for (final JsonNode node : nextNodes) {
				combinedNode.setAll((ObjectNode) node);
			}

			for (final JsonNode node : prevNodes) {
				combinedNode.setAll((ObjectNode) node);
			}

			response.put("json", combinedNode);

		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			response.put("error", jsonEx.getMessage());
		}

		return response;
	}

	private List<JsonNode> getPagedRows(String uri, HttpEntity<Object> request, List<JsonQuery> jqQueries)
			throws JsonQueryException {
		final List<JsonNode> nextRows = new ArrayList<JsonNode>();

		final ResponseEntity<JsonNode> couchResponse = restTemplate.exchange(uri, HttpMethod.GET, request,
				JsonNode.class);

		final JsonNode responseBody = couchResponse.getBody();

		for (final JsonQuery jq : jqQueries) {
			nextRows.addAll(jq.apply(responseBody));
		}

		return nextRows;
	}

	private String buildStartkeys(boolean areNextStartKeys, Long limit) {
		final StringBuilder sb = new StringBuilder();

		sb.append("if (.rows | length) >= ").append(limit);

		if (areNextStartKeys) {
			sb.append(" then {nextpages:[{startkey:.rows[");
		} else {
			sb.append(" then {prevpages:[{startkey:.rows[");
		}

		sb.append(JQ_LAST_ROW).append("].key, startkey_docid:.rows[").append(JQ_LAST_ROW)
				.append("].id}]} else {offset:0} end");

		return sb.toString();
	}
}
