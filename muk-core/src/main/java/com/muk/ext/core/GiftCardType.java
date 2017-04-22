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
