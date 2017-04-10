package com.muk.services.web.client;

import java.io.IOException;
import java.util.Date;

import javax.inject.Inject;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.client.RestTemplate;

import com.muk.ext.security.KeystoreService;

public class DefaultRequestInterceptor implements HttpRequestInterceptor {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultRequestInterceptor.class);


	@Inject
	@Qualifier("generalKeystoreService")
	private KeystoreService keystoreService;

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	private volatile String authTicket;
	private volatile Date authTicketExpiration;

	private final Object lockObject = new Object();

	@Override
	public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
	}

	public Date getAuthTicketExpiration() {
		synchronized (lockObject) {
			return authTicketExpiration;
		}
	}

	public void setAuthTicket(String authTicket) {
		synchronized (lockObject) {
			this.authTicket = authTicket;
		}
	}
}
