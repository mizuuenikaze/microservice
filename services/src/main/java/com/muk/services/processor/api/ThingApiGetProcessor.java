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
package com.muk.services.processor.api;

import org.apache.camel.Exchange;

import com.muk.ext.camel.processor.AbstractProcessor;
import com.muk.ext.core.json.RestThing;

public class ThingApiGetProcessor extends AbstractProcessor<Object, RestThing> {

	@Override
	protected RestThing forceFail(Exchange exchange) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Class<? extends Object> getBodyClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected RestThing handleExchange(Object body, Exchange exchange) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean propagateHeaders() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean propagateAttachments() {
		// TODO Auto-generated method stub
		return false;
	}
}
