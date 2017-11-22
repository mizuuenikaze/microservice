package com.muk.ext.core.json.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class BlogPage {
	private Long startkey;
	private String startkey_docid;

	public Long getStartkey() {
		return startkey;
	}

	public void setStartkey(Long startkey) {
		this.startkey = startkey;
	}

	public String getStartkey_docid() {
		return startkey_docid;
	}

	public void setStartkey_docid(String startkey_docid) {
		this.startkey_docid = startkey_docid;
	}
}
