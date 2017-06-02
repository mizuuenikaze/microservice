package com.muk.services.processor.api;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.web.util.UriComponents;

import com.muk.ext.core.json.model.PaymentRequest;
import com.muk.ext.core.json.model.PaymentResponse;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.facades.PaymentFacade;
import com.muk.services.processor.AbstractResourceProcessor;

public class PaymentApiProcessor extends AbstractResourceProcessor<PaymentRequest, PaymentResponse> {

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
	protected Map<String, Object> affect(PaymentRequest body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> paymentResponse = super.affect(body, exchange, redirectComponents);
		paymentResponse.put("error", "Invalid payment state.");

		if (ServiceConstants.PaymentState.start.equals(body.getCurrentStep())) {
			paymentResponse = paymentFacade.startPayment(body, redirectComponents);
		}

		return paymentResponse;
	}
}
