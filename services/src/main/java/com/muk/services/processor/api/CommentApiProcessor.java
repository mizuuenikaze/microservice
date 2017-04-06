package com.muk.services.processor.api;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.processor.AbstractRestProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.core.json.RestReply;
import com.muk.ext.core.json.model.UserComment;

public class CommentApiProcessor extends AbstractRestProcessor<UserComment, RestReply> {

	@Override
	protected RestReply forceFail(Exchange exchange) {
		RestReply reply = createResponse();
		reply.setMessage("Force Fail");
		return reply;
	}

	@Override
	protected Class<? extends UserComment> getBodyClass() {
		return UserComment.class;
	}

	@Override
	protected RestReply handleExchange(UserComment body, Exchange exchange) throws Exception {
		String httpMethod = (String) exchange.getIn().getHeader(Exchange.HTTP_METHOD);

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.valueOf(Status.SUCCESS_CREATED.getCode()));
		RestReply restReply = createResponse();

		restReply.setMessage("More complete object with hateos.");

		switch (httpMethod) {
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
			restReply.setMessage("http method not understood.");
		}

		return restReply;
	}

	@Inject
	@Qualifier("restBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<RestReply> beanGenerator) {
		super.setBeanGenerator(beanGenerator);
	}
}
