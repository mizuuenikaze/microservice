package com.muk.services.processor;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.services.api.ApiContextLoader;
import com.muk.services.api.QueueDemultiplexer;
import com.muk.services.api.model.ExtendedEvent;
import com.muk.services.exchange.NotificationEvent;
import com.muk.services.exchange.ServiceConstants;

/**
 *
 * Determines the activemq queue to route to for common events. Puts the event
 * id in the header for idempotent consumer.
 *
 */
public abstract class AbstractQueueDemultiplexer implements QueueDemultiplexer {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractQueueDemultiplexer.class);

	@Inject
	@Qualifier("mukContextLoader")
	private ApiContextLoader mukApiContextLoader;

	@Override
	public void routeToQueue(Exchange exchange) {
		String destination = "unknown";
		final ExtendedEvent event = exchange.getIn().getBody(ExtendedEvent.class);

		event.setTenantId(mukApiContextLoader.getApiContext().getTenantId());

		final String[] eventSplit = event.getTenantId().split("\\.");

		switch (eventSplit[0]) {
		case ServiceConstants.Codes.eventCatApplication:
			switch (eventSplit[1]) {
			case ServiceConstants.Codes.installedEvent:
				destination = ServiceConstants.QueueDestinations.queueAppInstalled;
				break;
			case ServiceConstants.Codes.uninstalledEvent:
				destination = ServiceConstants.QueueDestinations.queueAppUninstalled;
				break;
			case ServiceConstants.Codes.enabledEvent:
				destination = ServiceConstants.QueueDestinations.queueAppEnabled;
				break;
			case ServiceConstants.Codes.disabledEvent:
				destination = ServiceConstants.QueueDestinations.queueAppDisabled;
				break;
			case ServiceConstants.Codes.upgradedEvent:
				destination = ServiceConstants.QueueDestinations.queueAppUpgraded;
				break;
			default:
				destination = "unknown";
			}
			break;
		default:
			destination = determineEventDestination(eventSplit, event);
		}

		exchange.getIn().setHeader(NotificationEvent.Keys.queueDestination, destination);
		exchange.getIn().setHeader(NotificationEvent.Keys.mukEventId, event.getId());
	}

	protected abstract String determineEventDestination(String[] eventParts, ExtendedEvent event);
}
