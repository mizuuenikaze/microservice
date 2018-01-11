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
package com.muk.services.facades;

import java.util.Map;

import org.springframework.web.util.UriComponents;

import com.muk.ext.core.json.model.PaymentRequest;

public interface PaymentFacade {
	Map<String, Object> startPayment(PaymentRequest paymentRequest, UriComponents redirectComponents);

	Map<String, Object> commitPayment(PaymentRequest paymentRequest, UriComponents redirectComponents);
}
