package com.muk.ext.core;

import java.util.HashMap;
import java.util.Map;

public enum GiftCardType {
	PLASTIC(0), DIGITAL(1);

	private final int cardTypeId;
	private static Map<Integer, GiftCardType> map;

	static {
		map = new HashMap<Integer, GiftCardType>();
		map.put(Integer.valueOf(0), PLASTIC);
		map.put(Integer.valueOf(1), DIGITAL);
	}

	private GiftCardType(int cardTypeId) {
		this.cardTypeId = cardTypeId;
	}

	public int getCardTypeId() {
		return cardTypeId;
	}

	public static GiftCardType from(Integer id) {
		return map.get(id);
	}
}
