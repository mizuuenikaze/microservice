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
