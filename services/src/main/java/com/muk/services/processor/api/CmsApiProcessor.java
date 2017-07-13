package com.muk.services.processor.api;

import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.web.util.UriComponents;

import com.muk.ext.core.json.model.CmsDoc;
import com.muk.services.api.CmsService;
import com.muk.services.processor.AbstractResourceProcessor;

public class CmsApiProcessor extends AbstractResourceProcessor<CmsDoc, CmsDoc> {

	@Inject
	private CmsService cmsService;

	@Override
	protected Class<? extends CmsDoc> getBodyClass() {
		return CmsDoc.class;
	}

	@Override
	protected Class<? extends CmsDoc> getReturnClass() {
		return CmsDoc.class;
	}

	@Override
	protected Map<String, Object> fetch(CmsDoc body, Exchange exchange, UriComponents redirectComponents) {
		Map<String, Object> cmsResponse = super.fetch(body, exchange, redirectComponents);

		cmsResponse = cmsService.fetchDocById(exchange.getIn().getHeader("docId", String.class));

		return cmsResponse;
	}

}