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

import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.http.common.HttpMethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.muk.ext.core.json.model.AppointmentRequest;
import com.muk.ext.core.json.model.AppointmentResponse;
import com.muk.services.exchange.CamelRouteConstants;
import com.muk.services.exchange.RestConstants;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.processor.AbstractResourceProcessor;

import net.thisptr.jackson.jq.JsonQuery;
import net.thisptr.jackson.jq.exception.JsonQueryException;

public class AppointmentApiProcessor extends AbstractResourceProcessor<AppointmentRequest, AppointmentResponse> {
	private static final Logger LOG = LoggerFactory.getLogger(AppointmentApiProcessor.class);

	@Override
	protected Map<String, Object> affect(AppointmentRequest body, Exchange exchange, UriComponents redirectComponents) {
		final Map<String, Object> appointmentResponse = super.affect(body, exchange, redirectComponents);

		// Async routes are processed in activemq paths.
		// This processor responds to the client with hateoas links to the results of processing

		final StringBuilder sb = new StringBuilder();
		sb.append("{id: \"")
				.append(exchange.getIn().getHeader(CamelRouteConstants.MessageHeaders.camelUUid, String.class))
				.append("\", status: ").append(ServiceConstants.SimpleStates.pending)
				.append(", links: [{rel: \"self\", method: \"").append(HttpMethods.GET).append("\", href: \"")
				.append(RestConstants.Rest.apiVer).append("/action/tasks\"}]}");

		try {
			final JsonQuery jq = JsonQuery.compile(sb.toString());
			final List<JsonNode> hateoasLinks = jq.apply(JsonNodeFactory.instance.objectNode());
			appointmentResponse.put("json", hateoasLinks.get(0));
			appointmentResponse.remove("error");
		} catch (final JsonQueryException jqEx) {
			appointmentResponse.put("error", jqEx.getMessage());
			LOG.error("Failed to respond to request.", jqEx);
		}

		return appointmentResponse;
	}

	@Override
	protected Class<? extends AppointmentRequest> getBodyClass() {
		return AppointmentRequest.class;
	}

	@Override
	protected Class<? extends AppointmentResponse> getReturnClass() {
		return AppointmentResponse.class;
	}

}
