/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
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
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.ext.core.json.model.BlogSliceSummary;
import com.muk.ext.core.json.model.CmsDoc;
import com.muk.services.api.BlogService;
import com.muk.services.processor.AbstractResourceProcessor;

public class BlogApiProcessor extends AbstractResourceProcessor<Object, BlogSliceSummary> {

	@Inject
	private BlogService blogService;

	@Override
	protected Class<? extends Object> getBodyClass() {
		return CmsDoc.class;
	}

	@Override
	protected Class<? extends BlogSliceSummary> getReturnClass() {
		return BlogSliceSummary.class;
	}

	@Override
	protected Map<String, Object> fetch(Object body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> cmsResponse = super.fetch(body, exchange, redirectComponents);
		final UriComponentsBuilder queryStringBuilder = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader("CamelHttpUri", String.class));
		queryStringBuilder.replaceQuery(exchange.getIn().getHeader("CamelHttpQuery", String.class));
		final MultiValueMap<String, String> params = queryStringBuilder.build().getQueryParams();

		cmsResponse = blogService.fetchPagedSummaryView(params.getFirst("mode"),
				params.getFirst("startkey") != null ? Long.parseLong(params.getFirst("startkey")) : null,
				params.getFirst("startkeyDocId"),
				params.getFirst("limit") != null ? Long.parseLong(params.getFirst("limit")) : null,
				params.getFirst("offset") != null ? Long.parseLong(params.getFirst("offset")) : null);

		return cmsResponse;
	}

}