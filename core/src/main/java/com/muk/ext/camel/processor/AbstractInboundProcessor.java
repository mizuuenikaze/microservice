package com.muk.ext.camel.processor;

import com.muk.ext.status.ProcessStatus;
import com.muk.ext.status.Status;

public abstract class AbstractInboundProcessor<BodyType, ReturnType extends ProcessStatus>
		extends AbstractProcessor<BodyType, ReturnType> {

	protected ReturnType successStatus() {
		ReturnType processStatus = createResponse();
		processStatus.setStatus(Status.SUCCESS);
		processStatus.setMessage(this.getClass().getName() + " Completed Successfully");

		return processStatus;
	}
}