package com.muk.services.processor.api;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.camel.Exchange;
import org.restlet.data.Status;

import com.muk.ext.core.json.model.Badge;
import com.muk.ext.core.json.model.Feature;
import com.muk.services.processor.AbstractRestListProcessor;

public class FeatureApiProcessor extends AbstractRestListProcessor<Object, Feature> {

	@Override
	protected Class<? extends Object> getBodyClass() {
		return Object.class;
	}

	@Override
	protected List<Feature> handleExchange(Object body, Exchange exchange) throws Exception {
		final String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.valueOf(Status.SUCCESS_OK.getCode()));
		final List<Feature> restReply = createResponse();

		switch (httpMethod) {
		case "GET":
			final Badge portalSuccess = new Badge();
			portalSuccess.setLabel("success");
			portalSuccess.setGlyphicon("ok");
			portalSuccess.setHint("functional");
			final Badge portalInfo = new Badge();
			portalInfo.setLabel("info");
			portalInfo.setGlyphicon("wrench");
			portalInfo.setHint("under construction");
			final Badge portalDanger = new Badge();
			portalDanger.setLabel("dander");
			portalDanger.setGlyphicon("cloud");
			portalDanger.setHint("vapor-ware");

			final Feature one = new Feature();
			one.setId("1");
			one.setTitle("This Portal");
			one.setBadges(Arrays.asList(portalSuccess, portalInfo));
			final Feature two = new Feature();
			two.setId("2");
			two.setTitle("Scheduler");
			two.setBadges(Collections.singletonList(portalDanger));
			final Feature three = new Feature();
			three.setId("Checkout");
			three.setBadges(Collections.singletonList(portalDanger));

			restReply.add(one);
			restReply.add(two);
			restReply.add(three);
		case "POST":
			// create a new user comment;
			break;
		case "PUT":
			// update a user comment;
			break;
		case "PATCH":
			// incremental change comment;
			break;
		default:
			exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE,
					Integer.valueOf(Status.CLIENT_ERROR_BAD_REQUEST.getCode()));
		}

		return restReply;
	}
}