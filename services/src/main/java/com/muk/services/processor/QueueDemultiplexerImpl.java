package com.muk.services.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muk.services.api.model.ExtendedEvent;
import com.muk.services.exchange.ServiceConstants;

/**
 *
 * Determines the activemq queue to route too. Puts the event id in the header
 * for idempotent consumer.
 *
 */
public class QueueDemultiplexerImpl extends AbstractQueueDemultiplexer {
	private static final Logger LOG = LoggerFactory.getLogger(QueueDemultiplexerImpl.class);


	@Override
	protected String determineEventDestination(String[] eventParts, ExtendedEvent event) {
		String destination = "unknown";
		final long lastNotificationTime = 0l;

		switch (eventParts[0]) {
		case ServiceConstants.Codes.eventCatOrder:
			switch (eventParts[1]) {
			case ServiceConstants.Codes.openedEvent:
				destination = "mukOpenOrderEvent";
				break;
			}
			break;
		default:
			destination = "unknown";
		}


		return destination;
	}
}
