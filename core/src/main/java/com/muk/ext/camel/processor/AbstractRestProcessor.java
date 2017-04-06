package com.muk.ext.camel.processor;

import com.muk.ext.core.json.RestReply;

public abstract class AbstractRestProcessor<BodyType, ReturnType extends RestReply>
		extends AbstractProcessor<BodyType, ReturnType> {

	@Override
	protected boolean propagateHeaders() {
		return true;
	}

	@Override
	protected boolean propagateAttachments() {
		return true;
	}
}
