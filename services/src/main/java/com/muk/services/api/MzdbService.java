package com.muk.services.api;

import java.util.List;
import com.muk.ext.core.api.Dummy;

public interface MzdbService<T> {
	void installSchema() throws Exception;

	Dummy findEntityListByFullyQualifiedName(Dummy entityList) throws Exception;

	T findEntityById(String id) throws Exception;

	boolean addEntity(T entity) throws Exception;

	boolean updateEntity(T entity) throws Exception;

	void deleteEntity(String id) throws Exception;

	long count() throws Exception;

	Dummy getPagedEntities(Integer pageSize, Integer startIndex, List<T> results) throws Exception;

	Dummy getPagedEntities(Integer pageSize, Integer startIndex, String filter, List<T> results)
			throws Exception;

	void purge() throws Exception;
}
