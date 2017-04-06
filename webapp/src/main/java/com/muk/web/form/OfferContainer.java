package com.muk.web.form;

import java.util.List;

public class OfferContainer {
	List<OfferForm> forms;

	public OfferContainer() {
	}

	public OfferContainer(List<OfferForm> forms) {
		this.forms = forms;
	}

	public List<OfferForm> getForms() {
		return forms;
	}

	public void setForms(List<OfferForm> forms) {
		this.forms = forms;
	}

}
