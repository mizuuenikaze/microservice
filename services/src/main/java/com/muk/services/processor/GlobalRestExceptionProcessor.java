package com.muk.services.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.http.HttpStatus;

public class GlobalRestExceptionProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		final Throwable cause = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, HttpStatus.UNAUTHORIZED.value());
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, cause.getCause().getMessage());
	}

}
