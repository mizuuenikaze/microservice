package com.muk.services.processor.api;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.processor.AbstractProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.core.api.Dummy;


public class SettingApiGetProcessor extends AbstractProcessor<Object, Dummy> {

	@Override
	protected Dummy forceFail(Exchange exchange) {
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return createResponse();
	}

	@Override
	protected Class<? extends Object> getBodyClass() {
		return Dummy.class;
	}

	@Override
	protected Dummy handleExchange(Object body, Exchange exchange) throws Exception {
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

	@Inject
	@Qualifier("dummyBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<Dummy> beanGenerator) {
		super.setBeanGenerator(beanGenerator);
	}
}
