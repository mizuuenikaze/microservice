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
