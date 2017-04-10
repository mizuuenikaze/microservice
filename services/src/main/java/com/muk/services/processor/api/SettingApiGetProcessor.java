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

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.processor.AbstractProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.core.api.Dummy;


public class SettingApiGetProcessor extends AbstractProcessor<Object, Dummy> {

	@Override
	protected Dummy forceFail(Exchange exchange) {
		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
				Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));

		return createResponse();
	}

	@Override
	protected Class<? extends Object> getBodyClass() {
		return Dummy.class;
	}

	@Override
	protected Dummy handleExchange(Object body, Exchange exchange) throws Exception {
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

	@Inject
	@Qualifier("dummyBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<Dummy> beanGenerator) {
		super.setBeanGenerator(beanGenerator);
	}
}
