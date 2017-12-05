package com.muk.services.facades;

import java.util.List;

import org.apache.camel.CamelException;
import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;

public interface ActionApiFacade {
	void hashRequest(Exchange exchange) throws CamelException;

	void loadAction(Exchange exchange) throws CamelException;

	List<Endpoint> processAction(Exchange exchange) throws CamelException;

	void finalizeAction(Exchange exchange) throws CamelException;

}
