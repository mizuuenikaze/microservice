package com.muk.services.processor.api;

import com.muk.ext.core.json.model.BlogDoc;

public class BlogDocApiProcessor extends CouchDbDocProcessor<BlogDoc, BlogDoc> {

	@Override
	protected Class<? extends BlogDoc> getBodyClass() {
		return BlogDoc.class;
	}

	@Override
	protected Class<? extends BlogDoc> getReturnClass() {
		return BlogDoc.class;
	}
}