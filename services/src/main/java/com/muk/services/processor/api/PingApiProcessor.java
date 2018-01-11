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
package com.muk.services.processor.api;

import org.apache.camel.Exchange;
import org.restlet.data.Status;

import com.muk.ext.core.json.RestReply;
import com.muk.services.processor.AbstractRestProcessor;

public class PingApiProcessor extends AbstractRestProcessor<Object, RestReply> {

	@Override
	protected Class<? extends Object> getBodyClass() {
		return Object.class;
	}

	@Override
	protected Class<? extends RestReply> getReturnClass() {
		return RestReply.class;
	}

	@Override
	protected RestReply handleExchange(Object body, Exchange exchange) throws Exception {
		final String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.valueOf(Status.SUCCESS_OK.getCode()));
		final RestReply restReply = createResponse();

		switch (httpMethod) {
		case "GET":
			restReply.setMessage("API up.");
		case "POST":
			// create a new user comment;
			break;
		case "PUT":
			// update a user comment;
			break;
		case "PATCH":
			// incremental change comment;
			break;
		default:
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
					Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
			restReply.setMessage("http method not understood.");
		}

		return restReply;
	}
}
