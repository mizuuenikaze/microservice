package com.muk.ext.core.json;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestHateoasReply extends RestReply {
	private List<HateoasLink> links;

	public List<HateoasLink> getLinks() {
		return links;
	}

	public void setLinks(List<HateoasLink> links) {
		this.links = links;
	}

}
