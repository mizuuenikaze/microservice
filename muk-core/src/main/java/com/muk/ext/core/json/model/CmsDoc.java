package com.muk.ext.core.json.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.core.json.RestReply;

@JsonInclude(Include.NON_NULL)
public class CmsDoc extends RestReply {
	private String id;
	private JsonNode page;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public JsonNode getPage() {
		return page;
	}

	public void setPage(JsonNode page) {
		this.page = page;
	}

}
