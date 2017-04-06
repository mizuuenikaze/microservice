package com.muk.services.api;

/**
 * A rest client interface that performs an operation server side.
 *
 */
public interface IntentService<T, U> {
	boolean perform(String apiPath, T request, Class<U> responseType) throws Exception;

	boolean performStreaming(String apiPath, T entity, Class<U> responseType, boolean download) throws Exception;
}
