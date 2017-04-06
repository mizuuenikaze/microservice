package com.muk.services.strategy;

import java.math.BigDecimal;

public interface GiftCardBalanceStrategy {
	BigDecimal parseValue(String value);

	BigDecimal parseValue(Double value);
}
