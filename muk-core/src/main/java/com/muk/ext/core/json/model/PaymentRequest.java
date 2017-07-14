package com.muk.ext.core.json.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PaymentRequest {
	private String paymentMethod;
	private String currentStep;
	private String service;
	private Double price;
	private String paymentId;
	private String payerId;
	private String info;
	private List<Pair<String, String>> metadata;
	private String email;

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getCurrentStep() {
		return currentStep;
	}

	public void setCurrentStep(String currentStep) {
		this.currentStep = currentStep;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public String getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(String paymentId) {
		this.paymentId = paymentId;
	}

	public String getPayerId() {
		return payerId;
	}

	public void setPayerId(String payerId) {
		this.payerId = payerId;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public List<Pair<String, String>> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<Pair<String, String>> metadata) {
		this.metadata = metadata;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

}
