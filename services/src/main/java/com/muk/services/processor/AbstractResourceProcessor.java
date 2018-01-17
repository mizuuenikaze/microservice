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
package com.muk.services.processor;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public abstract class AbstractResourceProcessor<BodyType, ReturnType>
		extends AbstractRestProcessor<BodyType, ReturnType> {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractResourceProcessor.class);

	@Inject
	private ObjectMapper jsonObjectMapper;

	@Override
	protected ReturnType handleExchange(BodyType body, Exchange exchange) throws Exception {
		final String httpMethod = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);

		ReturnType response = createResponse();
		Map<String, Object> serviceResponse = new HashMap<String, Object>();
		final UriComponents redirectComponents = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class)).replaceQuery(null).build();

		// Consider async operations as primary
		int successCode = Status.SUCCESS_ACCEPTED.getCode();

		try {
			switch (httpMethod) {
			case "GET":
				serviceResponse = fetch(body, exchange, redirectComponents);
				// A get will always return actionable info, never an async operation
				successCode = Status.SUCCESS_OK.getCode();
				break;
			case "POST":
				serviceResponse = affect(body, exchange, redirectComponents);
				break;
			case "PUT":
				serviceResponse = replace(body, exchange, redirectComponents);
				break;
			case "PATCH":
				serviceResponse = applyDiff(body, exchange, redirectComponents);
				break;
			default:
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
						Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
			}

			if (serviceResponse.containsKey("error")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SERVER_ERROR_INTERNAL.getCode());
				final ObjectNode errorNode = jsonObjectMapper.createObjectNode();
				errorNode.put("message", (String) serviceResponse.get("error"));
				response = jsonObjectMapper.treeToValue((JsonNode) errorNode, getReturnClass());
			} else if (serviceResponse.containsKey("json")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, successCode);
				response = jsonObjectMapper.treeToValue((JsonNode) serviceResponse.get("json"), getReturnClass());
			} else if (serviceResponse.containsKey("success") && Boolean.TRUE.equals(serviceResponse.get("success"))) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_NO_CONTENT.getCode());
			} else if (!exchange.getOut().getHeaders().containsKey(Exchange.HTTP_RESPONSE_CODE)) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_NO_CONTENT.getCode());
			}
		} catch (final HttpClientErrorException httpClientEx) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, httpClientEx.getStatusCode().value());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, httpClientEx.getMessage());

			if (LOG.isDebugEnabled()) {
				LOG.debug(httpClientEx.getResponseBodyAsString());
			}
		}

		return response;
	}

	protected Map<String, Object> fetch(BodyType body, Exchange exchange, UriComponents redirectComponents) {
		final Map<String, Object> response = new HashMap<String, Object>();
		response.put("error", "Operation undefined for resource.");

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return response;
	}

	protected Map<String, Object> affect(BodyType body, Exchange exchange, UriComponents redirectComponents) {
		final Map<String, Object> response = new HashMap<String, Object>();
		response.put("error", "Operation undefined for resource.");

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return response;
	}

	protected Map<String, Object> replace(BodyType body, Exchange exchange, UriComponents redirectComponents) {
		final Map<String, Object> response = new HashMap<String, Object>();
		response.put("error", "Operation undefined for resource.");

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return response;
	}

	protected Map<String, Object> applyDiff(BodyType body, Exchange exchange, UriComponents redirectComponents) {
		final Map<String, Object> response = new HashMap<String, Object>();
		response.put("error", "Operation undefined for resource.");

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return response;
	}

	protected ObjectMapper getMapper() {
		return jsonObjectMapper;
	}
}
