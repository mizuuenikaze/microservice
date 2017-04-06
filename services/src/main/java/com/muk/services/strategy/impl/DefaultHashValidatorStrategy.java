package com.muk.services.strategy.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.commons.io.IOUtils;
import org.restlet.data.Header;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.muk.ext.core.ApplicationState;
import com.muk.ext.core.api.Dummy;
import com.muk.ext.security.KeystoreService;
import com.muk.ext.security.NonceService;
import com.muk.services.api.ApiContextLoader;
import com.muk.services.api.CrudService;
import com.muk.services.api.MozuConfigurationService;
import com.muk.services.api.builder.RestTemplateBuilder;
import com.muk.services.api.model.ExtendedEvent;
import com.muk.services.strategy.HashValidatorStrategy;

public class DefaultHashValidatorStrategy implements HashValidatorStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultHashValidatorStrategy.class);

	@Inject
	@Qualifier("hashService")
	private NonceService nonceService;

	@Inject
	@Qualifier("mukConfigurationService")
	private MozuConfigurationService mukConfigurationService;

	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Inject
	@Qualifier("mukContextLoader")
	private ApiContextLoader mukApiContextLoader;

	@Inject
	@Qualifier("applicationState")
	private ApplicationState appState;

	@Inject
	@Qualifier("thirdPartyDummyService")
	private CrudService<Dummy> thirdPartyDummyService;

	@Inject
	@Qualifier("jsonObjectMapper")
	private ObjectMapper objectMapper;

	@Override
	public void validateHash(Exchange exchange) {
		final List<Header> httpHeaders = exchange.getIn().getHeader("org.restlet.http.headers", List.class);

		String queryStringDate = null;
		String queryStringHash = null;
		String tenant = null;
		String body = null;
		final Map<String, String> apiHeaders = new HashMap<String, String>();

		if (httpHeaders != null) {
			for (final Header header : httpHeaders) {
				switch (header.getName()) {
				case ApiContextLoader.X_VOL_HMAC_SHA256:
					queryStringHash = header.getValue();
					break;
				case ApiContextLoader.DATE:
					queryStringDate = header.getValue();
					break;
				case ApiContextLoader.X_VOL_TENANT:
					tenant = header.getValue();
					break;
				}

				apiHeaders.put(header.getName(), header.getValue());
			}
		}

		mukApiContextLoader.storeLocalApiContext(apiHeaders);

		// Disable the app if we don't know about it
		if (!appState.exists(Integer.valueOf(tenant))) {
			final Map<String, String> headers = new HashMap<String, String>();

			try {
				final Dummy application = thirdPartyDummyService
						.read(RestTemplateBuilder.Api.thirdPartyApplication, null, null, Dummy.class);
				application.setEnabled(Boolean.FALSE);
				thirdPartyDummyService.update(RestTemplateBuilder.Api.thirdPartyApplication, application);
			} catch (final Exception apiEx) {
				LOG.error("Failed to disable tenant " + tenant, apiEx);
				exchange.getIn().setHeader("hashVerified", Boolean.FALSE);
				exchange.getIn().setHeader("hashResult", "tenant not verified.");
			}
		}

		if (queryStringDate != null && queryStringHash != null) {

			try {
				body = IOUtils.toString((InputStream) exchange.getIn().getBody(), StandardCharsets.ISO_8859_1.name());
			} catch (final IOException ioEx) {
				LOG.error("Failed to convert stream.", ioEx);
			}

			try {
				final String sharedSecret = keystoreService.getPBEKey(mukConfigurationService.getAppId());
				final String doubleSecret = nonceService.generateHash(sharedSecret + sharedSecret);
				final String payloadHash = nonceService.generateHash(doubleSecret.concat(queryStringDate).concat(body));

				if (isValidMozuAdminRequest(payloadHash, queryStringHash, queryStringDate)) {
					exchange.getIn().setHeader("hashVerified", Boolean.TRUE);
					mukApiContextLoader.storeLocalApiContext(apiHeaders, exchange);
					exchange.getIn().setBody(objectMapper.readValue(body, ExtendedEvent.class));
				} else {
					exchange.getIn().setHeader("hashVerified", Boolean.FALSE);
					exchange.getIn().setHeader("hashResult", "hash not valid");
				}
			} catch (final IOException ioEx) {
				LOG.error("Failed to generate hash.", ioEx);
				exchange.getIn().setHeader("hashVerified", Boolean.FALSE);
				exchange.getIn().setHeader("hashResult", ioEx.getMessage());
			} catch (final GeneralSecurityException secEx) {
				LOG.error("Failed to lookup secret.", secEx);
				exchange.getIn().setHeader("hashVerified", Boolean.FALSE);
				exchange.getIn().setHeader("hashResult", secEx.getMessage());
			}
		}
	}

	private boolean isValidMozuAdminRequest(String payloadHash, String messageHash, String dt) {
		final boolean isValid = messageHash.equals(payloadHash);

		// if (isValid) {
		// final org.joda.time.format.DateTimeFormatter dtf =
		// DateTimeFormat.forPattern("E, dd MMM yyyy HH:mm:ss zzz");
		// final DateTime dTime = dtf.parseDateTime(dt);
		//
		// final long deltaTime = (DateTime.now().getMillis() -
		// dTime.getMillis()) / 1000;
		// isValid = deltaTime <= 180;
		// }

		return isValid;
	}
}
