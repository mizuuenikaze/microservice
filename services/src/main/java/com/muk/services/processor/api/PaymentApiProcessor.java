package com.muk.services.processor.api;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.ext.core.json.model.PaymentRequest;
import com.muk.ext.core.json.model.PaymentResponse;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.facades.PaymentFacade;
import com.muk.services.processor.AbstractRestProcessor;

public class PaymentApiProcessor extends AbstractRestProcessor<PaymentRequest, PaymentResponse> {

	@Inject
	private ObjectMapper jsonObjectMapper;

	@Inject
	private PaymentFacade paymentFacade;

	@Override
	protected Class<? extends PaymentRequest> getBodyClass() {
		return PaymentRequest.class;
	}

	@Override
	protected Class<? extends PaymentResponse> getReturnClass() {
		return PaymentResponse.class;
	}

	@Override
	protected PaymentResponse handleExchange(PaymentRequest body, Exchange exchange) throws Exception {
		final String httpMethod = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);

		PaymentResponse response = createResponse();
		Map<String, Object> paymentResponse = new HashMap<String, Object>();
		final UriComponents redirectComponents = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class)).replaceQuery(null).build();

		try {
			switch (httpMethod) {
			case "POST":
				if (ServiceConstants.PaymentState.start.equals(body.getCurrentStep())) {
					paymentResponse = paymentFacade.startPayment(body, redirectComponents);
				} else if (ServiceConstants.PaymentState.commit.equals(body.getCurrentStep())) {
					paymentResponse = paymentFacade.commitPayment(body, redirectComponents);
				}
				break;
			default:
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
						Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
			}

			if (paymentResponse.containsKey("error")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SERVER_ERROR_INTERNAL.getCode());
				final ObjectNode errorNode = jsonObjectMapper.createObjectNode();
				errorNode.put("message", (String) paymentResponse.get("error"));
				response = jsonObjectMapper.treeToValue((JsonNode) errorNode, PaymentResponse.class);
			} else if (paymentResponse.containsKey("json")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_ACCEPTED.getCode());
				response = jsonObjectMapper.treeToValue((JsonNode) paymentResponse.get("json"), PaymentResponse.class);
			} else {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_NO_CONTENT.getCode());
			}
		} catch (final HttpClientErrorException httpClientEx) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, httpClientEx.getStatusCode().value());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, httpClientEx.getMessage());
		}

		return response;
	}

}
