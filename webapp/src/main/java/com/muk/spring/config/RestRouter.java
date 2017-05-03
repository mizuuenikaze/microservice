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
package com.muk.spring.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestPropertyDefinition;
import org.apache.camel.spring.SpringRouteBuilder;
import org.restlet.data.MediaType;
import org.restlet.engine.util.StringUtils;

import com.muk.ext.core.json.RestReply;
import com.muk.services.exchange.CamelRouteConstants;
import com.muk.services.exchange.RestConstants;
import com.muk.services.json.RouteAction;
import com.muk.services.processor.RefreshTokenProcessor;
import com.muk.services.processor.RouteActionProcessor;
import com.muk.services.processor.api.FeatureApiProcessor;
import com.muk.services.processor.api.PingApiProcessor;

/**
 *
 * Rest configuration of camel routes.
 *
 * Security --- The backing authentication and authorization server is uaa for
 * oauth2 so we define an login endpoint and all other protected endpoints
 * follow the same pipeline of providing a principal subject, using spring
 * security, and handling token refreshes before doing any business logic.
 *
 */
public class RestRouter extends SpringRouteBuilder {

	@Override
	public void configure() throws Exception {
		final List<RestPropertyDefinition> corsHeaders = new ArrayList<RestPropertyDefinition>();
		RestPropertyDefinition corsHeader = new RestPropertyDefinition();
		corsHeader.setKey("Access-Control-Allow-Origin");
		corsHeader.setValue("*");
		corsHeaders.add(corsHeader);

		corsHeader = new RestPropertyDefinition();
		corsHeader.setKey("Access-Control-Allow-Methods");
		corsHeader.setValue("GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, CONNECT, PATCH");
		corsHeaders.add(corsHeader);

		corsHeader = new RestPropertyDefinition();
		corsHeader.setKey("Access-Control-Allow-Headers");
		corsHeader.setValue(
				"Origin, Accept, Authorization, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
		corsHeaders.add(corsHeader);

		corsHeader = new RestPropertyDefinition();
		corsHeader.setKey("Access-Control-Max-Age");
		corsHeader.setValue("3600");
		corsHeaders.add(corsHeader);

		restConfiguration().component("restlet").bindingMode(RestBindingMode.json).skipBindingOnErrorCode(false)
		.dataFormatProperty("json.in.moduleClassNames", "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
		.dataFormatProperty("json.out.moduleClassNames", "com.fasterxml.jackson.datatype.jsr310.JavaTimeModule")
		.dataFormatProperty("json.in.USE_BIG_DECIMAL_FOR_FLOATS", "true").enableCORS(false)
		.setCorsHeaders(corsHeaders);

		// notification endpoint
		rest(RestConstants.Rest.apiPath).post("/intents").bindingMode(RestBindingMode.off)
		.consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.APPLICATION_JSON.getName()).route()
		.routeId(CamelRouteConstants.RouteIds.asyncNotificationPush).process("authPrincipalProcessor")
		.policy("restUserPolicy").process(StringUtils.firstLower(RefreshTokenProcessor.class.getSimpleName()))
		.to("direct:intent");

		// camel route intents
		rest(RestConstants.Rest.adminPath).post("/routes/changeRouteState").type(RouteAction.class)
		.outType(RestReply.class).consumes(MediaType.APPLICATION_JSON.getName())
		.produces(MediaType.APPLICATION_JSON.getName()).route().process("authPrincipalProcessor")
		.policy("restUserPolicy").process(StringUtils.firstLower(RefreshTokenProcessor.class.getSimpleName()))
		.to("direct:routeConfiguration");

		// oauth2 token users
		rest(RestConstants.Rest.adminPath).get("/tokenLogin").outType(RestReply.class)
		.produces(MediaType.APPLICATION_JSON.getName()).route()
		.process(StringUtils.firstLower(RefreshTokenProcessor.class.getSimpleName()))
		.bean("statusHandler", "logRestStatus");

		// api
		rest(RestConstants.Rest.apiPath).get("/ping").outType(RestReply.class)
		.consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.APPLICATION_JSON.getName()).route()
		.process("authPrincipalProcessor").policy("restUserPolicy")
		.process(StringUtils.firstLower(RefreshTokenProcessor.class.getSimpleName())).to("direct:ping");

		rest(RestConstants.Rest.apiPath).get("/features").outTypeList(RestReply.class)
		.consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.APPLICATION_JSON.getName()).route()
		.process("authPrincipalProcessor").policy("restUserPolicy")
		.process(StringUtils.firstLower(RefreshTokenProcessor.class.getSimpleName())).to("direct:feature");

		// direct rest routes

		// camel route management
		from("direct:routeConfiguration").process(StringUtils.firstLower(RouteActionProcessor.class.getSimpleName()))
		.bean("statusHandler", "logRestStatus");

		// api routes
		from("direct:ping").process(StringUtils.firstLower(PingApiProcessor.class.getSimpleName()))
		.bean("statusHandler", "logRestStatus");
		from("direct:feature").process(StringUtils.firstLower(FeatureApiProcessor.class.getSimpleName()))
		.bean("statusHandler", "logRestStatus");
	}
}
