package com.muk.services.strategy;

import org.apache.camel.Exchange;

public interface HashValidatorStrategy {
	void validateHash(Exchange exchange);

}
