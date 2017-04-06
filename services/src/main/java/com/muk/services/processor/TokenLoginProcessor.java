package com.muk.services.processor;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.commons.lang3.StringUtils;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Qualifier;

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
		final String authorizationCode = exchange.getIn().getHeader("authorizationCode", String.class);
		final TokenResponse response = userDetailService.loadByAuthorizationCode(authorizationCode);

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
