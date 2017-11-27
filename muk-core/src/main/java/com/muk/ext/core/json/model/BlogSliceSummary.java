package com.muk.ext.core.json.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.json.RestReply;

@JsonInclude(Include.NON_NULL)
public class BlogSliceSummary extends RestReply {
	private List<BlogPage> prevpages;
	private List<BlogPage> nextpages;
	private List<BlogEntrySummary> rows;
	private Long offset;

	public List<BlogPage> getPrevpages() {
		return prevpages;
	}

	public void setPrevpages(List<BlogPage> prevpages) {
		this.prevpages = prevpages;
	}

	public List<BlogPage> getNextpages() {
		return nextpages;
	}

	public void setNextpages(List<BlogPage> nextpages) {
		this.nextpages = nextpages;
	}

	public List<BlogEntrySummary> getRows() {
		return rows;
	}

	public void setRows(List<BlogEntrySummary> rows) {
		this.rows = rows;
	}

	public Long getOffset() {
		return offset;
	}

	public void setOffset(Long offset) {
		this.offset = offset;
	}
}
