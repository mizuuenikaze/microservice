package com.muk.services.api;

public interface SubCrudService<T> {
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
	boolean subUpsert(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception;

	boolean subInsert(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception;

	boolean subUpdate(String apiPath, String parentId, T entity) throws Exception;

	T subRead(String apiPath, String parentId, T entity, Class<T> responseType) throws Exception;

	boolean subDelete(String apiPath, String parentId, T entity) throws Exception;

}
