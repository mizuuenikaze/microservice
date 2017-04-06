package com.muk.services.api.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PublishDraft {
	private boolean allPending;
	private String productCodes;
	private String publishSetCode;

	public boolean isAllPending() {
		return allPending;
	}

	public void setAllPending(boolean allPending) {
		this.allPending = allPending;
	}

	public String getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}

	public String getPublishSetCode() {
		return publishSetCode;
	}

	public void setPublishSetCode(String publishSetCode) {
		this.publishSetCode = publishSetCode;
	}
}
