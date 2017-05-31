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

import javax.security.auth.Subject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;

import com.muk.services.exchange.RestConstants;
import com.muk.services.security.BearerAuthenticationToken;

/**
 * Extracts bearer token from Auth header for camel
 */
public class BearerTokenAuthPrincipalProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		String bearerToken = RestConstants.Rest.anonymousToken;

		if (exchange.getIn().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
			bearerToken = StringUtils
					.substringAfter(exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION, String.class), "Bearer ");
		}

		// create an Authentication object
		// build a new bearer token type
		final BearerAuthenticationToken authToken = new BearerAuthenticationToken(bearerToken);

		// wrap it in a Subject
		final Subject subject = new Subject();
		subject.getPrincipals().add(authToken);

		// place the Subject in the In message
		exchange.getIn().setHeader(Exchange.AUTHENTICATION, subject);
	}
}
