package com.muk.services.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.GiftCardStatus;
import com.muk.ext.core.json.RestHateoasReply;

@JsonInclude(Include.NON_NULL)
public class GiftCard extends RestHateoasReply {
	private String id;
	private String currency;
	private Double amount;
	private Double initialAmount;
	private GiftCardStatus status;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Double getAmount() {
		return amount;
	}

	public void setAmount(Double amount) {
		this.amount = amount;
	}

	public Double getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(Double initialAmount) {
		this.initialAmount = initialAmount;
	}

	public GiftCardStatus getStatus() {
		return status;
	}

	public void setStatus(GiftCardStatus status) {
		this.status = status;
	}
}
