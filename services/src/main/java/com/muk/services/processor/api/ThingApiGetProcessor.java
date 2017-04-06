package com.muk.services.processor.api;

import org.apache.camel.Exchange;

import com.muk.ext.camel.processor.AbstractProcessor;
import com.muk.ext.core.json.RestThing;

public class ThingApiGetProcessor extends AbstractProcessor<Object, RestThing> {

	@Override
	protected RestThing forceFail(Exchange exchange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends Object> getBodyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RestThing handleExchange(Object body, Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean propagateHeaders() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean propagateAttachments() {
		// TODO Auto-generated method stub
		return false;
	}
}
