package com.muk.ext.core.json;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class RestThing {
	private String thingy;

	public String getThingy() {
		return thingy;
	}

	public void setThingy(String thingy) {
		this.thingy = thingy;
	}
}
