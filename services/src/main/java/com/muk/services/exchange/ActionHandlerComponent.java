package com.muk.services.exchange;

import java.util.Map;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.ProcessorEndpoint;

import com.muk.ext.core.json.model.ActionDoc;
import com.muk.ext.core.json.model.AppointmentRequest;

/**
 * Provides endpoint providers for dynamic action processing.
 *
 */
public class ActionHandlerComponent extends DefaultComponent {

	@Override
	protected Endpoint createEndpoint(String uri, String remaining, Map<String, Object> parameters) throws Exception {
		final Endpoint endpoint = new ProcessorEndpoint(uri, this, new Processor() {

			@Override
			public void process(Exchange exchange) throws Exception {
				final ActionDoc doc = exchange.getIn().getBody(ActionDoc.class);
				ActionStrategy strategy = null;

				if (AppointmentRequest.class.getName().equals(remaining)) {
					strategy = schedulerService;
				}

				if (strategy != null) {
					strategy.performAction(doc);
					exchange.getIn().setBody(doc);
				}
			}
		});

		return endpoint;
	}

}
