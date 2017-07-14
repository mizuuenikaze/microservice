package com.muk.services.facades.impl;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.tuple.Pair;
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

	@Inject
	PaymentService stripePaymentService;

	@Override
	public Map<String, Object> startPayment(PaymentRequest paymentRequest, UriComponents redirectComponents) {
		Map<String, Object> response = new HashMap<String, Object>();
		final ObjectNode payload = JsonNodeFactory.instance.objectNode();

		if (ServiceConstants.PaymentMethods.paypalExpress.equals(paymentRequest.getPaymentMethod())) {
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
		} else if (ServiceConstants.PaymentMethods.stripe.equals(paymentRequest.getPaymentMethod())) {
			payload.put("amount", (long) Math.floor(paymentRequest.getPrice() * 100d));
			payload.put("currency", "usd");
			payload.put("description", paymentRequest.getService());
			payload.put("source", paymentRequest.getInfo());
			payload.put("receipt_email", paymentRequest.getEmail());

			if (paymentRequest.getMetadata() != null) {
				final ObjectNode mds = payload.putObject("metadata");

				for (final Pair<String, String> pair : paymentRequest.getMetadata()) {
					mds.put(pair.getLeft(), pair.getRight());
				}
			}

			response = stripePaymentService.startPayment(payload);

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
