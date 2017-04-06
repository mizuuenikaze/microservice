package com.muk.ext.core;

import java.util.HashMap;
import java.util.Map;

public enum GiftCardStatus {
	AVAILABLE(0), ACTIVE(1), INVALID(2), EXTERNAL(3);

	private final int cardStatusId;
	private static Map<Integer, GiftCardStatus> map;

	static {
		map = new HashMap<Integer, GiftCardStatus>();
		map.put(Integer.valueOf(0), AVAILABLE);
		map.put(Integer.valueOf(1), ACTIVE);
		map.put(Integer.valueOf(2), INVALID);
	}

	private GiftCardStatus(int cardStatusId) {
		this.cardStatusId = cardStatusId;
	}

	public int getCardStatusId() {
		return cardStatusId;
	}

	public static GiftCardStatus from(Integer id) {
		return map.get(id);
	}
}
