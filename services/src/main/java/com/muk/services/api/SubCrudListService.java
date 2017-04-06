package com.muk.services.api;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

public interface SubCrudListService<T> {
	/**
	 * Update or insert on a sub compositional member.
	 *
	 * @param parentId
	 *            Identifier of the enclosing object.
	 * @param entity
	 *            The object to upsert
	 * @return
	 * @throws Exception
	 */
	boolean subUpsert(String apiPath, String parentId, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

	boolean subInsert(String apiPath, String parentId, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

	boolean subUpdate(String apiPath, String parentId, List<T> entity) throws Exception;

	List<T> subRead(String apiPath, String parentId, List<T> entity, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

	boolean subDelete(String apiPath, String parentId, List<T> entity) throws Exception;

}
