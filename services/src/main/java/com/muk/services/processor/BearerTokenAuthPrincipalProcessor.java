package com.muk.services.processor;

import java.util.List;

import javax.security.auth.Subject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.restlet.data.Header;

import com.muk.services.security.BearerAuthenticationToken;

/**
 * Extracts bearer token from Auth header for camel
 */
public class BearerTokenAuthPrincipalProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		final List<Header> httpHeaders = exchange.getIn().getHeader("org.restlet.http.headers", List.class);

		String bearerToken = "badToken";
		for (final Header header : httpHeaders) {
			if (header.getName().toLowerCase().equals(HttpHeaders.AUTHORIZATION.toLowerCase())) {
				bearerToken = StringUtils.substringAfter(header.getValue(), "Bearer: ");
				break;
			}
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
