package com.muk.services.api.model;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.muk.ext.core.api.Dummy;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ExtendedEvent extends Dummy {

	private LocalDateTime timestamp;

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}

}
