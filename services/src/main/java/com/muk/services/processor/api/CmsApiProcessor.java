package com.muk.services.processor.api;

import com.muk.ext.core.json.model.CmsDoc;

public class CmsApiProcessor extends CouchDbDocProcessor<CmsDoc, CmsDoc> {

	@Override
	protected Class<? extends CmsDoc> getBodyClass() {
		return CmsDoc.class;
	}

	@Override
	protected Class<? extends CmsDoc> getReturnClass() {
		return CmsDoc.class;
	}
}