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

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.muk.ext.camel.processor.AbstractRestProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.core.json.model.oauth.TokenResponse;
import com.muk.services.api.CachingOauthUserDetailsService;

/**
 * A authorization_code is expected so that a new bearer token can be requested
 * and a user refreshed in the user cache.
 *
 */
public class TokenLoginProcessor extends AbstractRestProcessor<Object, TokenResponse> {
	private final static Logger LOG = LoggerFactory.getLogger(TokenLoginProcessor.class);

	@Inject
	@Qualifier("oauthUserDetailService")
	private CachingOauthUserDetailsService userDetailService;

	@Override
	protected TokenResponse forceFail(Exchange exchange) {
		final TokenResponse reply = createResponse();
		reply.setMessage("Force fail.");

		return reply;
	}

	@Override
	protected Class<? extends Object> getBodyClass() {
		return Object.class;
	}

	@Override
	protected TokenResponse handleExchange(Object body, Exchange exchange) throws Exception {
		String authorizationCode = "";
		final UriComponents redirectComponents = UriComponentsBuilder.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class)).replaceQuery(null).build();

		if (exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class) == HttpMethod.GET.name()) {
			final UriComponents uriComponents = UriComponentsBuilder.newInstance()
					.query(exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class)).build();
			authorizationCode = uriComponents.getQueryParams().getFirst("authorizationCode");
		} else {
			authorizationCode = exchange.getIn().getHeader("authorizationCode", String.class);
		}

		final TokenResponse response = userDetailService.loadByAuthorizationCode(authorizationCode, redirectComponents.toUriString());

		if (StringUtils.isNotBlank(response.getMessage())) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
					Integer.valueOf(Status.CLIENT_ERROR_FORBIDDEN.getCode()));
		} else {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.valueOf(Status.SUCCESS_OK.getCode()));
		}

		return response;
	}

	@Inject
	@Qualifier("tokenResponseBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<TokenResponse> beanGenerator) {
		super.setBeanGenerator(beanGenerator);

	}
}
