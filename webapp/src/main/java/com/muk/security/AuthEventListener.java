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
package com.muk.security;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.context.ApplicationListener;
import org.springframework.security.access.event.AuthorizedEvent;
import org.springframework.security.core.Authentication;

import com.muk.services.exchange.RestConstants;

/**
 *
 * When the token is refreshed, this needs to get back to the client for subsequent calls. This listener gets the
 * published event from the authorization policy so that the latest user details can be compared against the incoming
 * token.
 *
 * This is outside of any camel routes so think about thread safety.
 *
 */
public class AuthEventListener implements ApplicationListener<AuthorizedEvent> {

	@Override
	public void onApplicationEvent(AuthorizedEvent event) {
		final Exchange exchange = (Exchange) event.getSource();

		if (!RestConstants.Rest.anonymousToken.equals(event.getAuthentication().getName())
				&& exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION, String.class) != null) {
			final Authentication currentAuthentication = event.getAuthentication();
			final String currentTokenRepresentation = (String) currentAuthentication.getCredentials();
			final String incomingTokenRepresentation = StringUtils
					.substringAfter(exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION, String.class), "Bearer ");

			if (!currentTokenRepresentation.equals(incomingTokenRepresentation)) {
				exchange.getOut().getHeaders().put(RestConstants.Headers.refreshToken, currentTokenRepresentation);
			}
		}
	}
}
