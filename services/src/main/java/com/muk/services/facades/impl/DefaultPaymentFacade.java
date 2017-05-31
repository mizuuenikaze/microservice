package com.muk.services.facades.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.util.UriComponents;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.ext.core.json.model.PaymentRequest;
import com.muk.services.api.PaymentService;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.facades.PaymentFacade;

public class DefaultPaymentFacade implements PaymentFacade {
	@Inject
	PaymentService paypalPaymentService;

	@Override
	public Map<String, Object> startPayment(PaymentRequest paymentRequest, UriComponents redirectComponents) {
		Map<String, Object> response = new HashMap<String, Object>();

		if (ServiceConstants.PaymentMethods.paypalExpress.equals(paymentRequest.getPaymentMethod())) {
			final ObjectNode payload = JsonNodeFactory.instance.objectNode();

			payload.put("intent", "sale");
			final ObjectNode redirectUrls = payload.putObject("redirect_urls");
			redirectUrls.put("return_url", redirectComponents.toUriString() + "/id/redirect");
			redirectUrls.put("cancel_url", redirectComponents.toUriString() + "/id/cancel");
			final ObjectNode payer = payload.putObject("payer");
			payer.put("payment_method", "paypal");
			final ArrayNode transactions = payload.putArray("transactions");
			final ObjectNode transaction = transactions.addObject();
			transaction.put("description", paymentRequest.getService());
			transaction.putObject("amount").put("total", paymentRequest.getPrice()).put("currency", "USD");

			response = paypalPaymentService.startPayment(payload);
		}

		return response;
	}

	@Override
	public Map<String, Object> commitPayment(PaymentRequest paymentRequest, UriComponents redirectComponents) {
		Map<String, Object> response = new HashMap<String, Object>();

		if (ServiceConstants.PaymentMethods.paypalExpress.equals(paymentRequest.getPaymentMethod())) {
			response = paypalPaymentService.commitPayment(paymentRequest.getPaymentId(), paymentRequest.getPayerId());
		}

		return response;

	}

}
