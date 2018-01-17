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

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muk.ext.camel.processor.AbstractInboundProcessor;
import com.muk.ext.status.ProcessStatus;

public abstract class StartingProcessor<BodyType, ReturnType extends ProcessStatus>
extends AbstractInboundProcessor<BodyType, ReturnType> {

	@Inject
	@Qualifier("jsonObjectMapper")
	private ObjectMapper jsonObjectMapper;

	@Override
	protected ReturnType handleExchange(BodyType body, Exchange exchange) throws Exception {


		return successStatus();

	}
}
