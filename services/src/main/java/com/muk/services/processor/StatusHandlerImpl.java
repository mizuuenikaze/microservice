package com.muk.services.processor;

import java.util.List;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muk.ext.core.json.RestReply;
import com.muk.ext.status.ProcessStatus;
import com.muk.ext.status.Status;
import com.muk.services.api.StatusHandler;

public class StatusHandlerImpl implements StatusHandler {
	private static final Logger LOG = LoggerFactory.getLogger(StatusHandlerImpl.class);

	@Override
	public void logProcessStatus(ProcessStatus processStatus) {
		if (processStatus != null) {
			if (Status.SUCCESS != processStatus.getStatus() && processStatus.hasCause()) {
				LOG.error(processStatus.getMessage(), processStatus.getCause());
			} else {
				LOG.info(processStatus.getMessage());
			}
		} else {
			LOG.error("Status Unknown");
		}
	}

	@Override
	public void logAggregateProcessStatus(List<ProcessStatus> groupedStatuses) {
		if (groupedStatuses != null && !groupedStatuses.isEmpty()) {
			ProcessStatus status = groupedStatuses.get(groupedStatuses.size() - 1);
			logProcessStatus(status);
		}
	}

	@Override
	public void logRestStatus(Exchange exchange) {
		String message = exchange.getIn().getBody(RestReply.class).getMessage();
		Integer code = exchange.getIn().getHeader(Exchange.HTTP_RESPONSE_CODE, Integer.class);

		if (code > org.restlet.data.Status.REDIRECTION_MULTIPLE_CHOICES.getCode()) {
			LOG.error(message);
		} else {
			LOG.info(message);
		}
	}
}
