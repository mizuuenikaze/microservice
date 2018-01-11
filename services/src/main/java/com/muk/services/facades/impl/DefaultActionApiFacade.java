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
package com.muk.services.facades.impl;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.CamelException;
import org.apache.camel.CamelExchangeException;
import org.apache.camel.Component;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.component.jackson.JacksonConstants;
import org.apache.camel.component.restlet.RestletConstants;
import org.restlet.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.HttpClientErrorException;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.core.json.model.ActionDoc;
import com.muk.ext.security.NonceService;
import com.muk.services.api.DocService;
import com.muk.services.exchange.CamelRouteConstants;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.facades.ActionApiFacade;

/**
 * Wraps pipeline logic to persist async request status and act upon the request.
 *
 * Persistence store is couchdb and processing is based on the inbound request type.
 *
 */
public class DefaultActionApiFacade implements ActionApiFacade {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultActionApiFacade.class);

	@Inject
	@Qualifier("hashService")
	private NonceService nonceService;

	@Inject
	private DocService docService;

	@Inject
	private Component processorEndpointComponent;

	@Override
	public void setupProperties(Exchange exchange) throws CamelException {
		final StringBuilder sb = new StringBuilder();
		sb.append(exchange.getIn().getHeader(RestletConstants.RESTLET_REQUEST, Request.class).getClientInfo()
				.getAddress()).append(exchange.getIn().getBody(String.class));

		try {
			exchange.getIn().setHeader(CamelRouteConstants.MessageHeaders.actionId,
					nonceService.generateHash(sb.toString()));
			exchange.getIn().setHeader(CamelRouteConstants.MessageHeaders.camelUUid,
					exchange.getContext().getUuidGenerator().generateUuid());
		} catch (final NoSuchAlgorithmException algEx) {
			throw new CamelExchangeException("Failed to hash request.", exchange, algEx);
		}
	}

	@Override
	public void loadAction(Exchange exchange) throws CamelException {
		final ActionDoc doc = new ActionDoc();
		doc.setId(exchange.getIn().getHeader(CamelRouteConstants.MessageHeaders.camelUUid, String.class));
		doc.setPayload(exchange.getIn().getBody());
		doc.setStatus(ServiceConstants.SimpleStates.pending);
		doc.setTimestamp(String.valueOf(System.currentTimeMillis()));

		Map<String, Object> docResponse = null;

		try {
			docResponse = docService.addDoc("action", doc);
			doc.setRevision(((JsonNode) docResponse.get("json")).get("rev").asText());
		} catch (final HttpClientErrorException httpClientEx) {
			doc.setMessage(httpClientEx.getStatusCode().value() + " - " + httpClientEx.getMessage());
			doc.setStatus(ServiceConstants.SimpleStates.error);

			LOG.error(doc.getMessage());
		}

		exchange.getIn().setBody(doc);

	}

	@Override
	public List<Endpoint> processAction(Exchange exchange) throws CamelException {
		final ActionDoc doc = exchange.getIn().getBody(ActionDoc.class);
		final List<Endpoint> endpoints = new ArrayList<Endpoint>();

		if (ServiceConstants.SimpleStates.error != doc.getStatus()) {
			try {
				endpoints.add(processorEndpointComponent.createEndpoint(
						"dynaProcessor:" + exchange.getIn().getHeader(JacksonConstants.UNMARSHAL_TYPE, String.class)));
			} catch (final Exception e) {
				throw new CamelExchangeException("Failed to create endpoint.", exchange, e);
			}
		}

		return endpoints;
	}

	@Override
	public void finalizeAction(Exchange exchange) {
		final ActionDoc doc = exchange.getIn().getBody(ActionDoc.class);

		if (ServiceConstants.SimpleStates.pending != doc.getStatus()) {
			Map<String, Object> docResponse = null;

			try {
				docResponse = docService.updateDoc("action", doc.getId(), doc);
				doc.setRevision(((JsonNode) docResponse.get("json")).get("rev").asText());
			} catch (final HttpClientErrorException httpClientEx) {
				doc.setMessage(httpClientEx.getStatusCode().value() + " - " + httpClientEx.getMessage());
				doc.setStatus(ServiceConstants.SimpleStates.error);

				LOG.error(doc.getMessage());
			}

		}
	}

}
