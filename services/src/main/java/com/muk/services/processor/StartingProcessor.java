package com.muk.services.processor;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muk.ext.camel.processor.AbstractInboundProcessor;
import com.muk.ext.core.api.Dummy;
import com.muk.ext.status.ProcessStatus;
import com.muk.services.api.ApiContextLoader;

public abstract class StartingProcessor<BodyType, ReturnType extends ProcessStatus>
extends AbstractInboundProcessor<BodyType, ReturnType> {

	@Inject
	@Qualifier("mukContextLoader")
	private ApiContextLoader mukApiContextLoader;

	@Inject
	@Qualifier("jsonObjectMapper")
	private ObjectMapper jsonObjectMapper;

	@Override
	protected ReturnType handleExchange(BodyType body, Exchange exchange) throws Exception {
		// Initialize muk context on this thread
		mukApiContextLoader.storeLocalApiContext(new Dummy());

		return successStatus();

	}
}
