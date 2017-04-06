package com.muk.ext.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.muk.ext.core.Action;

@JsonInclude(Include.NON_NULL)
public class SystemAction {
	private Action action;

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}
}
