/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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
package com.muk.services.exchange;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.impl.DefaultComponent;
import org.apache.camel.impl.ProcessorEndpoint;

import com.muk.ext.core.json.model.ActionDoc;
import com.muk.ext.core.json.model.AppointmentRequest;
import com.muk.services.strategy.ActionStrategy;

/**
 * Provides endpoint providers for dynamic action processing.
 *
 */
public class ActionHandlerComponent extends DefaultComponent {

	@Inject
	private ActionStrategy schedulerService;

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
