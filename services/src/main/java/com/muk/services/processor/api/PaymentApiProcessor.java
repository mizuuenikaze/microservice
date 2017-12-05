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

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.web.util.UriComponents;

import com.muk.ext.core.json.model.PaymentRequest;
import com.muk.ext.core.json.model.PaymentResponse;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.facades.PaymentFacade;
import com.muk.services.processor.AbstractResourceProcessor;

public class PaymentApiProcessor extends AbstractResourceProcessor<PaymentRequest, PaymentResponse> {

	@Inject
	private PaymentFacade paymentFacade;

	@Override
	protected Class<? extends PaymentRequest> getBodyClass() {
		return PaymentRequest.class;
	}

	@Override
	protected Class<? extends PaymentResponse> getReturnClass() {
		return PaymentResponse.class;
	}

	@Override
	protected Map<String, Object> affect(PaymentRequest body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> paymentResponse = super.affect(body, exchange, redirectComponents);
		paymentResponse.put("error", "Invalid payment state.");

		if (ServiceConstants.PaymentState.start.equals(body.getCurrentStep())) {
			paymentResponse = paymentFacade.startPayment(body, redirectComponents);
		}

		return paymentResponse;
	}
}
