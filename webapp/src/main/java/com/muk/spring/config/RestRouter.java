package com.muk.spring.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.camel.model.rest.RestBindingMode;
import org.apache.camel.model.rest.RestPropertyDefinition;
import org.apache.camel.spring.SpringRouteBuilder;
import org.restlet.data.MediaType;

import com.muk.ext.core.json.RestReply;
import com.muk.ext.core.json.RestThing;
import com.muk.ext.core.json.model.UserComment;
import com.muk.services.exchange.CamelRouteConstants;
import com.muk.services.exchange.RestConstants;
import com.muk.services.json.RouteAction;

/**
 *
 * Rest configuration of camel routes.
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
		.dataFormatProperty("json.in.USE_BIG_DECIMAL_FOR_FLOATS", "true").enableCORS(true)
		.setCorsHeaders(corsHeaders);
		/*
		 * rest(RestConstants.Rest.adminPath).verb("patch",
		 * "/system").type(SystemAction.class).outType(RestReply.class)
		 * .consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.
		 * APPLICATION_JSON.getName()).route()
		 * .process("authPrincipalProcessor").policy("restUserPolicy").to(
		 * "direct:systemConfiguration");
		 */
		// notification endpoint
		rest(RestConstants.Rest.notificationPath).post().bindingMode(RestBindingMode.off)
		.consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.APPLICATION_JSON.getName()).route()
		.routeId(CamelRouteConstants.RouteIds.asyncNotificationPush).process("authPrincipalProcessor")
		.policy("restUserPolicy").to("direct:mukEvent");

		// camel route intents
		rest(RestConstants.Rest.adminPath).post("/routes/changeRouteState").type(RouteAction.class)
		.outType(RestReply.class).consumes(MediaType.APPLICATION_JSON.getName())
		.produces(MediaType.APPLICATION_JSON.getName()).route().process("authPrincipalProcessor")
		.policy("restUserPolicy").to("direct:routeConfiguration");

		// oauth2 token users
		rest(RestConstants.Rest.adminPath).get("/tokenLogin/{authorizationCode}").outType(RestReply.class)
		.produces(MediaType.APPLICATION_JSON.getName()).route().process("tokenLoginProcessor")
		.bean("statusHandler", "logRestStatus");

		// api
		rest(RestConstants.Rest.apiPath).get("/thing/{id}").outType(RestThing.class)
		.produces(MediaType.APPLICATION_JSON.getName()).route().process("authPrincipalProcessor")
		.policy("restUserPolicy").to("direct:thingGetApi");

		rest(RestConstants.Rest.apiPath).post("/usercomment").type(UserComment.class).outType(RestReply.class)
		.consumes(MediaType.APPLICATION_JSON.getName()).produces(MediaType.APPLICATION_JSON.getName()).route()
		.process("authPrincipalProcessor").policy("restUserPolicy").to("direct:commentApi");

		// direct rest routes

		// system actions
		from("direct:systemConfiguration").process("nopProcessor").bean("statusHandler", "logRestStatus");

		// camel route management
		from("direct:routeConfiguration").process("routeActionProcessor").bean("statusHandler", "logRestStatus");

		// api routes
		from("direct:settingGetApi").process("settingApiGetProcessor").bean("statusHandler", "logRestStatus");
		from("direct:thingGetApi").process("thingApiGetProcessor").bean("statusHandler", "logRestStatus");
		from("direct:commentApi").process("commentApiProcessor").bean("statusHandler", "logRestStatus");
	}
}
