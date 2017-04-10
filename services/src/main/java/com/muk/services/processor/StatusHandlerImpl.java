/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *******************************************************************************/
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
