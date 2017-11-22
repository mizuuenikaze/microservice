package com.muk.services.processor.api;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.services.api.CmsService;
import com.muk.services.processor.AbstractResourceProcessor;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public abstract class CouchDbDocProcessor<DOCIN, DOCOUT> extends AbstractResourceProcessor<DOCIN, DOCOUT> {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbDocProcessor.class);

	@Inject
	private CmsService cmsService;

	@Override
	protected Map<String, Object> fetch(DOCIN body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> cmsResponse = super.fetch(body, exchange, redirectComponents);
		final UriComponentsBuilder uriBuilder = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader("CamelHttpUri", String.class));

		final String db = uriBuilder.build().getPathSegments().get(3);
		JsonQuery jq = null;

		try {
			switch (db) {
			case "cms":
				jq = JsonQuery.compile("{id: ._id, page: .sections}");
				break;
			case "blog":
				jq = JsonQuery.compile(
						"{id: ._id, timestamp: .timestamp, title: .title, subtitle: .subtitle, keywords: .keywords, body: .body}");
				break;
			default:
				jq = JsonQuery.compile(".");
			}

			cmsResponse = cmsService.fetchDocById(db, exchange.getIn().getHeader("docId", String.class), jq);
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			cmsResponse.put("error", jsonEx.getMessage());
		}
		return cmsResponse;
	}

}