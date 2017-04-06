package com.muk.services.api;

import org.apache.camel.Exchange;

public interface QueueDemultiplexer {
	void routeToQueue(Exchange exchange);

}
