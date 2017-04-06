package com.muk.services.dataimport;

import java.util.Map;

import com.muk.services.strategy.TranslationFactoryStrategy;
import com.muk.services.strategy.TranslationStrategy;

public class ImportTranslationFactoryStrategy implements TranslationFactoryStrategy {
	private Map<String, TranslationStrategy<?, ?>> translationStrategyMap;

	@Override
	public TranslationStrategy<?, ?> findTranslationStrategy(String key) {
		return translationStrategyMap.get(key);
	}

	public void setTranslationStrategyMap(Map<String, TranslationStrategy<?, ?>> translationStrategyMap) {
		this.translationStrategyMap = translationStrategyMap;
	}
}
