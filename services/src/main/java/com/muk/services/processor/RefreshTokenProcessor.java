package com.muk.services.processor;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.muk.services.exchange.RestConstants;

/**
 *
 * Compares the incoming bearer token with the current authentication context.
 * If it is different, then the token was refreshed and needs to get back to the
 * client.
 *
 */
public class RefreshTokenProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {

		if (exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION, String.class) != null) {
			final Authentication currentAuthentication = SecurityContextHolder.getContext().getAuthentication();
			final String currentTokenRepresentation = (String) currentAuthentication.getCredentials();
			final String incomingTokenRepresentation = StringUtils
					.substringAfter(exchange.getIn().getHeader(HttpHeaders.AUTHORIZATION, String.class), "Bearer ");

			if (!currentTokenRepresentation.equals(incomingTokenRepresentation)) {
				exchange.getIn().getHeaders().put(RestConstants.Headers.refreshToken, currentTokenRepresentation);
			}
		}
	}

}
