package com.muk.ext.core.json.model;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class PatchRequest {
	private String stateChange;
	private List<Pair<String, Object>> pathChanges;

	public String getStateChange() {
		return stateChange;
	}

	public void setStateChange(String stateChange) {
		this.stateChange = stateChange;
	}

	public List<Pair<String, Object>> getPathChanges() {
		return pathChanges;
	}

	public void setPathChanges(List<Pair<String, Object>> pathChanges) {
		this.pathChanges = pathChanges;
	}

}
