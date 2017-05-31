package com.muk.services.facades;

import java.util.Map;

import org.springframework.web.util.UriComponents;

import com.muk.ext.core.json.model.PaymentRequest;

public interface PaymentFacade {
	Map<String, Object> startPayment(PaymentRequest paymentRequest, UriComponents redirectComponents);

	Map<String, Object> commitPayment(PaymentRequest paymentRequest, UriComponents redirectComponents);
}
