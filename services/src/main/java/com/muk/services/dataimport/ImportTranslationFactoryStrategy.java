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
package com.muk.services.dataimport;

import java.util.Map;

import com.muk.services.strategy.TranslationFactoryStrategy;
import com.muk.services.strategy.TranslationStrategy;

public class ImportTranslationFactoryStrategy implements TranslationFactoryStrategy {
	private Map<String, TranslationStrategy<?, ?>> translationStrategyMap;

	@SuppressWarnings("unchecked")
	@Override
	public TranslationStrategy<?, ?> findTranslationStrategy(String key) {
		return translationStrategyMap.get(key);
	}

	public void setTranslationStrategyMap(Map<String, TranslationStrategy<?, ?>> translationStrategyMap) {
		this.translationStrategyMap = translationStrategyMap;
	}
}
