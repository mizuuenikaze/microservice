package com.muk.services.api;

import java.util.List;

import org.apache.camel.Exchange;

import com.muk.ext.status.ProcessStatus;

public interface StatusHandler {
	void logProcessStatus(ProcessStatus processStatus);

	void logAggregateProcessStatus(List<ProcessStatus> groupedStatuses);

	void logRestStatus(Exchange exchange);
}
