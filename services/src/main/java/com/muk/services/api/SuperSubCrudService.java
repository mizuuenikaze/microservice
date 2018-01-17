/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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

import com.muk.ext.core.api.Identifier;

public interface SuperSubCrudService<T> {
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
	boolean subUpsert(String apiPath, String parentId, T entity, Identifier<T> identifier, Class<T> responseType)
			throws Exception;

	boolean subInsert(String apiPath, String parentId, T entity, Identifier<T> identifier, Class<T> responseType)
			throws Exception;

	boolean subUpdate(String apiPath, String parentId, T entity, Identifier<T> identifier) throws Exception;

	T subRead(String apiPath, String parentId, T entity, Identifier<T> identifier, Class<T> responseType)
			throws Exception;

	boolean subDelete(String apiPath, String parentId, T entity, Identifier<T> identifier) throws Exception;

}
