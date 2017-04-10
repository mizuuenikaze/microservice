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
