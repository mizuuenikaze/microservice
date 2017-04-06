package com.muk.ext.status;

/**
 * 
 * A structure to contain the status information for any generic process.
 * 
 */

public class ProcessStatus {
	private Status status;
	private String message;
	private Throwable cause;

	public boolean hasCause() {
		return cause != null;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}
}
