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
package com.muk.services.processor.api;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.services.api.DocService;
import com.muk.services.processor.AbstractResourceProcessor;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public abstract class CouchDbDocProcessor<DOCIN, DOCOUT> extends AbstractResourceProcessor<DOCIN, DOCOUT> {
	private static final Logger LOG = LoggerFactory.getLogger(CouchDbDocProcessor.class);

	@Inject
	private DocService docService;

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
				jq = JsonQuery.compile("{id: ._id, timestamp, title, subtitle, keywords, hasHtml, body, image}");
				break;
			case "action":
				jq = JsonQuery.compile("{id: ._id, status, message}");
				break;
			default:
				jq = JsonQuery.compile(".");
			}

			cmsResponse = docService.fetchDocById(db, exchange.getIn().getHeader("docId", String.class), jq);
		} catch (final JsonQueryException jsonEx) {
			LOG.error("failed jq", jsonEx);
			cmsResponse.put("error", jsonEx.getMessage());
		}
		return cmsResponse;
	}

}