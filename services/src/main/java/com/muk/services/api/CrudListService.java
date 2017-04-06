package com.muk.services.api;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

/**
 * Basic crud actions
 *
 */
public interface CrudListService<T> {
	boolean insert(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	boolean update(String apiPath, List<T> entity) throws Exception;

	boolean update(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	boolean delete(String apiPath, List<T> entity) throws Exception;

	List<T> read(String apiPath, List<T> entityTemplate, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

	boolean upsert(String apiPath, List<T> entity, ParameterizedTypeReference<List<T>> responseType) throws Exception;
}
