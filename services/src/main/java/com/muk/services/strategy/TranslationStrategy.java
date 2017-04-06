package com.muk.services.strategy;

/**
 *
 * Translations will implement custom logic to take source data to target data.
 * Subclasses are not spring beans and thus are not injected.
 *
 * @param <T>
 *            Source type
 * @param <U>
 *            Target type
 */
public interface TranslationStrategy<T, U> {
	U translate(T source);
}
