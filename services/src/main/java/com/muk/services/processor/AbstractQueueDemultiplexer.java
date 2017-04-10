/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
package com.muk.services.processor;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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


	@Override
	public void routeToQueue(Exchange exchange) {
		String destination = "unknown";
		final ExtendedEvent event = exchange.getIn().getBody(ExtendedEvent.class);


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
