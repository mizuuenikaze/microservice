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
package com.muk.ext.camel.processor;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

/**
 * Basic framework for all processors with list return types.
 *
 * @param <BodyType>
 *            The expected type of the in message.
 * @param <ListType>
 *            The generic list type to return.
 * @param <ReturnType>The
 *            return type of the result provided to the out message.
 */
public abstract class AbstractListProcessor<BodyType, ReturnType> implements Processor {

	/**
	 * Force error for QA
	 */
	private boolean killSwitch;

	@Override
	public void process(Exchange exchange) throws Exception {
		// LOG.info("Entering: " + this.getClass().getName());
		List<ReturnType> result = null;

		if (Boolean.TRUE.equals(exchange.getIn().getHeader("killSwitch", Boolean.class))) {
			result = forceFail(exchange);
			exchange.getIn().removeHeader("killSwitch");
		} else {
			final BodyType body = extractBody(exchange);
			result = handleExchange(body, exchange);
		}

		exchange.getOut().setBody(result);

		transferHeaders(exchange);
		transferAttachments(exchange);
	}

	protected BodyType extractBody(Exchange exchange) {
		if (getBodyClass() != null) {
			return exchange.getIn().getBody(getBodyClass());
		}

		return null;
	}

	protected void transferHeaders(Exchange exchange) {
		if (propagateHeaders()) {
			exchange.getOut().getHeaders().putAll(exchange.getIn().getHeaders());
		}
	}

	protected void transferAttachments(Exchange exchange) {
		if (propagateAttachments()) {
			exchange.getOut().getAttachments().putAll(exchange.getIn().getAttachments());
		}
	}

	protected List<ReturnType> createResponse() {
		return new ArrayList<ReturnType>();
	}

	public boolean isKillSwitch() {
		return killSwitch;
	}

	public void setKillSwitch(boolean killSwitch) {
		this.killSwitch = killSwitch;
	}

	protected abstract List<ReturnType> forceFail(Exchange exchange);

	protected abstract Class<? extends BodyType> getBodyClass();

	protected abstract List<ReturnType> handleExchange(BodyType body, Exchange exchange) throws Exception;

	protected abstract boolean propagateHeaders();

	protected abstract boolean propagateAttachments();

}
