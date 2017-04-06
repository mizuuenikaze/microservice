package com.muk.services.strategy;

public interface TranslationFactoryStrategy {
	<T, U> TranslationStrategy<T, U> findTranslationStrategy(String key);
}
