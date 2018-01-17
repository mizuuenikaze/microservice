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
package com.muk.services.processor;

import java.util.List;

import org.apache.camel.Exchange;
import org.apache.camel.RuntimeCamelException;
import org.restlet.data.Status;

import com.muk.ext.camel.processor.AbstractListProcessor;

public abstract class AbstractRestListProcessor<BodyType, ReturnType>
extends AbstractListProcessor<BodyType, ReturnType> {

	@Override
	protected List<ReturnType> forceFail(Exchange exchange) {
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.SERVER_ERROR_INTERNAL.getCode()));
		throw new RuntimeCamelException("Force Fail");
	}

	@Override
	protected boolean propagateHeaders() {
		return false;
	}

	@Override
	protected boolean propagateAttachments() {
		return true;
	}
}
