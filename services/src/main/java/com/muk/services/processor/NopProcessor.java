package com.muk.services.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

public class NopProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		// nothing to do - exchange passthrough

	}

}
