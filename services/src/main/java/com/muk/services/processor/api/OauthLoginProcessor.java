package com.muk.services.processor.api;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.muk.ext.core.json.HateoasLink;
import com.muk.ext.core.json.model.OauthLoginRequest;
import com.muk.ext.core.json.model.OauthLoginResponse;
import com.muk.services.api.CachingOauthUserDetailsService;
import com.muk.services.api.UaaLoginService;
import com.muk.services.processor.AbstractRestProcessor;

import net.thisptr.jackson.jq.JsonQuery;

/**
 *
 * Processes a login sequence with uaa
 *
 */
public class OauthLoginProcessor extends AbstractRestProcessor<OauthLoginRequest, OauthLoginResponse> {

	@Inject
	private UaaLoginService uaaLoginService;

	@Inject
	@Qualifier("oauthUserDetailsService")
	private CachingOauthUserDetailsService userDetailsService;

	@Inject
	private ObjectMapper jsonObjectMapper;

	@Override
	protected Class<? extends OauthLoginRequest> getBodyClass() {
		return OauthLoginRequest.class;
	}

	@Override
	protected Class<? extends OauthLoginResponse> getReturnClass() {
		return OauthLoginResponse.class;
	}

	@Override
	protected OauthLoginResponse handleExchange(OauthLoginRequest body, Exchange exchange) throws Exception {
		final String httpMethod = exchange.getIn().getHeader(Exchange.HTTP_METHOD, String.class);
		OauthLoginResponse response = createResponse();
		Map<String, Object> tokenResponse = new HashMap<String, Object>();
		final UriComponents redirectComponents = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class)).replaceQuery(null).build();

		try {
			switch (httpMethod) {
			case "POST":
				// start login process
				final Map<String, Object> loginResponse = uaaLoginService.loginForClient(body.getUsername(),
						body.getPassword(), body.getClientId(), redirectComponents);

				if (loginResponse.containsKey("json")) {
					// client access needs approval
					JsonQuery jq = JsonQuery.compile(".options|.confirm|.key");
					final List<JsonNode> approveNodes = jq.apply((JsonNode) loginResponse.get("json"));
					final UriComponentsBuilder hrefConfirmBuilder = UriComponentsBuilder
							.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class))
							.replaceQuery(null);
					hrefConfirmBuilder.queryParam(approveNodes.get(0).textValue(), "true");
					final UriComponentsBuilder hrefDenyBuilder = UriComponentsBuilder
							.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class))
							.replaceQuery(null);
					hrefDenyBuilder.queryParam(approveNodes.get(0).textValue(), "false");

					jq = JsonQuery.compile(".undecided_scopes|.[]|.code");
					final List<JsonNode> scopeNodes = jq.apply((JsonNode) loginResponse.get("json"));

					int index = 0;
					for (final JsonNode node : scopeNodes) {
						hrefConfirmBuilder.queryParam("scope." + index, node.textValue());
						hrefDenyBuilder.queryParam("scope." + index, node.textValue());
						index++;
					}

					final List<HateoasLink> links = new ArrayList<HateoasLink>();
					HateoasLink link = new HateoasLink();
					link.setRel("confirm");
					link.setMethod(HttpMethod.GET.name());
					link.setHref(hrefConfirmBuilder.build().toUriString());
					links.add(link);

					link = new HateoasLink();
					link.setRel("deny");
					link.setMethod(HttpMethod.GET.name());
					link.setHref(hrefDenyBuilder.build().toUriString());
					links.add(link);

					response.setLinks(links);

					exchange.getOut().setHeader(HttpHeaders.SET_COOKIE, loginResponse.get(HttpHeaders.SET_COOKIE));
					exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_OK.getCode());
				} else if (loginResponse.containsKey("error")) {
					tokenResponse.put("error", loginResponse.get("error"));
				} else {
					// already approved - get access token
					tokenResponse = userDetailsService.loadByAuthorizationCode((String) loginResponse.get("code"),
							redirectComponents.toUriString());
				}
				break;
			case "GET":
				// approve the client
				final String authorizationCode = uaaLoginService.approveClient(
						exchange.getIn().getHeader(Exchange.HTTP_QUERY, String.class),
						exchange.getIn().getHeader(HttpHeaders.COOKIE, String.class));

				if (authorizationCode != null) {
					// get access token
					tokenResponse = userDetailsService.loadByAuthorizationCode(authorizationCode,
							redirectComponents.toUriString());
				}

				exchange.getOut().setHeader(HttpHeaders.SET_COOKIE, clearCookies(exchange));
				break;
			default:
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
						Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
			}

			if (tokenResponse.containsKey("error")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.CLIENT_ERROR_UNAUTHORIZED.getCode());
				final ObjectNode errorNode = jsonObjectMapper.createObjectNode();
				errorNode.put("message", (String) tokenResponse.get("error"));
				response = jsonObjectMapper.treeToValue((JsonNode) errorNode, OauthLoginResponse.class);
			} else if (tokenResponse.containsKey("json")) {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_OK.getCode());
				response = jsonObjectMapper.treeToValue((JsonNode) tokenResponse.get("json"), OauthLoginResponse.class);
			} else {
				exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Status.SUCCESS_NO_CONTENT.getCode());
			}
		} catch (final HttpClientErrorException httpClientEx) {
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, httpClientEx.getStatusCode().value());
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_TEXT, httpClientEx.getMessage());
			exchange.getOut().setHeader(HttpHeaders.SET_COOKIE, clearCookies(exchange));
		}

		return response;
	}

	private List<String> clearCookies(Exchange exchange) {
		final OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
		final String cookieExpires = DateTimeFormatter.RFC_1123_DATE_TIME.format(now);

		final UriComponents uri = UriComponentsBuilder
				.fromUriString(exchange.getIn().getHeader(Exchange.HTTP_URI, String.class)).build();
		final List<String> cookies = new ArrayList<String>();
		//		cookies.add("X-Uaa-Csrf=;path=/"
		//				+ StringUtils.join(uri.getPathSegments().subList(0, uri.getPathSegments().size() - 2), "/")
		//				+ ";max-age=0;expires=" + cookieExpires);
		cookies.add("X-Uaa-Csrf=;path=" + uri.getPath() + ";max-age=0;expires=" + cookieExpires);
		cookies.add("JSESSIONID=;path=" + uri.getPath() + ";max-age=0;expires=" + cookieExpires);
		cookies.add("Current-User=;path=" + uri.getPath() + ";max-age=0;expires=" + cookieExpires);

		return cookies;
	}
}
