package com.muk.ext.core.json.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class Badge {
	private String label;
	private String glyphicon;
	private String hint;

	public String getGlyphicon() {
		return glyphicon;
	}

	public void setGlyphicon(String glyphicon) {
		this.glyphicon = glyphicon;
	}

	public String getHint() {
		return hint;
	}

	public void setHint(String hint) {
		this.hint = hint;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}
