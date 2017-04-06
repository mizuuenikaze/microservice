package com.muk.ext.camel;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;

import com.muk.ext.status.ProcessStatus;

public class StatusListAggregationStrategy extends AbstractListAggregationStrategy<ProcessStatus> {

	@Override
	public ProcessStatus getValue(Exchange exchange) {
		return exchange.getIn().getBody(ProcessStatus.class);
	}
}
