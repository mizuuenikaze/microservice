package com.muk.ext.camel.processor;

import org.apache.camel.Exchange;

import com.muk.ext.status.ProcessStatus;
import com.muk.ext.status.Status;

public abstract class AbstractInternalProcessor<BodyType extends ProcessStatus, ReturnType extends ProcessStatus>
		extends AbstractProcessor<BodyType, ReturnType> {
	@Override
	public void process(Exchange exchange) throws Exception {
		try {
			super.process(exchange);
		} catch (Exception e) {
			ReturnType processStatus = createResponse();
			processStatus.setStatus(Status.ERROR);
			processStatus.setCause(e);
			processStatus.setMessage("An unexpected error occured with gift card activation.");
			propagateStatus(exchange, processStatus);

			exchange.getOut().setBody(processStatus);

			transferHeaders(exchange);
			transferAttachments(exchange);
		}
	}

	@Override
	protected boolean propagateHeaders() {
		return true;
	}

	@Override
	protected boolean propagateAttachments() {
		return true;
	}

	protected ReturnType successStatus() {
		ReturnType processStatus = createResponse();
		processStatus.setStatus(Status.SUCCESS);
		processStatus.setMessage(this.getClass().getName() + " Completed Successfully");

		return processStatus;
	}

	protected abstract void propagateStatus(Exchange exchange, ReturnType status);
}
