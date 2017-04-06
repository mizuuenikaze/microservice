package com.muk.services.exchange;

import com.muk.ext.status.ProcessStatus;

/**
 * Adds a status code for ease of conditional processing.
 * 
 */
public class ServiceProcessStatus extends ProcessStatus {
	private ServiceError errorCode;

	public ServiceError getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(ServiceError errorCode) {
		this.errorCode = errorCode;
	}

}
