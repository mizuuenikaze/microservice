package com.muk.services.processor;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.services.api.NotificationPollService;
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

	@Inject
	@Qualifier("notificationPollService")
	private NotificationPollService notificationPollService;

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

		if (!"unknown".equals(destination)) {
			try {
				notificationPollService.updateLastSuccessfulNotifcationUtc(lastNotificationTime);
			} catch (final Exception e) {
				LOG.error("Failed to update notification polled time", e);
			}
		}

		return destination;
	}
}
