package com.muk.web.form;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

public class OfferForm {
	@NotNull
	private String pricelistId;
	@NotNull
	@Min(1)
	private String productCode;
	private String segment;
	private String offerId;
	private String offerType;
	private String miles;
	private String cash;
	private String activeStart;
	private String activeEnd;
	private Integer pageSize;
	private Integer pageIndex;
	private String deleteUrl;

	public String getPricelistId() {
		return pricelistId;
	}

	public void setPricelistId(String pricelistId) {
		this.pricelistId = pricelistId;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getOfferId() {
		return offerId;
	}

	public void setOfferId(String offerId) {
		this.offerId = offerId;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	public String getMiles() {
		return miles;
	}

	public void setMiles(String miles) {
		this.miles = miles;
	}

	public String getCash() {
		return cash;
	}

	public void setCash(String cash) {
		this.cash = cash;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public Integer getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getActiveStart() {
		return activeStart;
	}

	public void setActiveStart(String activeStart) {
		this.activeStart = activeStart;
	}

	public String getActiveEnd() {
		return activeEnd;
	}

	public void setActiveEnd(String activeEnd) {
		this.activeEnd = activeEnd;
	}

	public String getDeleteUrl() {
		return deleteUrl;
	}

	public void setDeleteUrl(String deleteUrl) {
		this.deleteUrl = deleteUrl;
	}
}
