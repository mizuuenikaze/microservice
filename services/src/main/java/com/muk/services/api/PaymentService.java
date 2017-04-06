package com.muk.services.api;

import java.util.List;

public interface PaymentService {
	List<String> getActions(String orderId, String paymentId) throws Exception;

}
