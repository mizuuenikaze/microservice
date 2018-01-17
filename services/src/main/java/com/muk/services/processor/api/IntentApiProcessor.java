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

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.core.json.RestHateoasReply;
import com.muk.ext.core.json.model.PatchRequest;
import com.muk.ext.core.json.model.PaymentRequest;
import com.muk.services.facades.PaymentFacade;
import com.muk.services.processor.AbstractResourceProcessor;

import net.thisptr.jackson.jq.JsonQuery;

/**
 *
 * This processor is resource agnostic since the request type is the same for all resources. Since the purpose is to
 * take an an action on a resource, usually external, It does not change a local representation, but executes some kind
 * of operation that results in a state change.
 *
 * Always on a PATCH request.
 *
 */
public class IntentApiProcessor extends AbstractResourceProcessor<PatchRequest, RestHateoasReply> {
	private static final Logger LOG = LoggerFactory.getLogger(IntentApiProcessor.class);

	@Inject
	private PaymentFacade paymentFacade;

	@Override
	protected Class<? extends PatchRequest> getBodyClass() {
		return PatchRequest.class;
	}

	@Override
	protected Class<? extends RestHateoasReply> getReturnClass() {
		return RestHateoasReply.class;
	}

	@Override
	protected Map<String, Object> applyDiff(PatchRequest body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> response = super.applyDiff(body, exchange, redirectComponents);

		/* Paypal centric for now since this is the first */
		//TODO generalize
		final String paymentId = exchange.getIn().getHeader("rId", String.class);

		if ("execute".equals(body.getStateChange())) {
			final StringBuilder jqBuilder = new StringBuilder();

			body.getPathChanges().add(Pair.of(".paymentId", "\"" + paymentId + "\""));

			for (final Pair<String, Object> jqStatement : body.getPathChanges()) {
				jqBuilder.append(jqStatement.getLeft()).append("|=").append(jqStatement.getRight()).append(" | ");
			}

			jqBuilder.append(".");
			final String jqString = jqBuilder.toString();
			//jqString = StringUtils.chop(jqString);

			try {
				final JsonQuery jq = JsonQuery.compile(jqString);
				final JsonNode node = getMapper().valueToTree(new PaymentRequest());
				final List<JsonNode> patched = jq.apply(node);

				response = paymentFacade.commitPayment(getMapper().treeToValue(patched.get(0), PaymentRequest.class),
						redirectComponents);
			} catch (final JsonProcessingException jsonEx) {
				LOG.error("Failed in json processing", jsonEx);
				response.put("error", "internal error");
			}
		}

		return response;
	}

}
