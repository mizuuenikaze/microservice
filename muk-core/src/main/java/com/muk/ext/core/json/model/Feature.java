package com.muk.ext.core.json.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.json.HateoasLink;

@JsonInclude(Include.NON_NULL)
public class Feature {
	private String id;
	private String title;
	private String content;
	private List<Badge> badges;
	private List<HateoasLink> _links;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public List<Badge> getBadges() {
		return badges;
	}

	public void setBadges(List<Badge> badges) {
		this.badges = badges;
	}

	public List<HateoasLink> get_links() {
		return _links;
	}

	public void set_links(List<HateoasLink> _links) {
		this._links = _links;
	}

}
