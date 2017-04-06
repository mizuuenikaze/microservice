package com.muk.services.processor;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.security.auth.Subject;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.restlet.data.Header;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

/**
 * Extracts Basic Auth header for camel
 */
public class BasicAuthPrincipalProcessor implements Processor {

	@Override
	public void process(Exchange exchange) throws Exception {
		List<Header> httpHeaders = exchange.getIn().getHeader("org.restlet.http.headers", List.class);

		String userpass = "bad:creds";
		for (Header header : httpHeaders) {
			if (header.getName().toLowerCase().equals(HttpHeaders.AUTHORIZATION.toLowerCase())) {
				userpass = new String(
						Base64.decodeBase64(
								(StringUtils.substringAfter(header.getValue(), " ").getBytes(StandardCharsets.UTF_8))),
						StandardCharsets.UTF_8);
				break;
			}
		}

		String[] tokens = userpass.split(":");

		// create an Authentication object
		// build a new bearer token type
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(tokens[0], tokens[1]);

		// wrap it in a Subject
		Subject subject = new Subject();
		subject.getPrincipals().add(authToken);

		// place the Subject in the In message
		exchange.getIn().setHeader(Exchange.AUTHENTICATION, subject);
	}
}
