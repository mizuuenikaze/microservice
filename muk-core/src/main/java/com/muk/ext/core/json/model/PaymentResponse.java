package com.muk.ext.core.json.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.json.RestHateoasReply;

@JsonInclude(Include.NON_NULL)
public class PaymentResponse extends RestHateoasReply {
	private String paymentId;
	private String state;

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
