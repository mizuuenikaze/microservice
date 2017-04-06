package com.muk.services.api;

import org.springframework.util.MultiValueMap;

/**
 * Basic crud actions
 *
 */
public interface CrudService<T> {
	boolean insert(String apiPath, T entity, Class<T> responseType) throws Exception;

	boolean update(String apiPath, T entity) throws Exception;

	boolean delete(String apiPath, T entity) throws Exception;

	T read(String apiPath, T entityTemplate, MultiValueMap<String, String> parameters, Class<T> responseType)
			throws Exception;

	boolean upsert(String apiPath, T entity, Class<T> responseType) throws Exception;
}
