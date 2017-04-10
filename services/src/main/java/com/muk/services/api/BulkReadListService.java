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

import java.util.Collection;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

/**
 * The wrapper type is actually in a list internally
 *
 * @author vole
 *
 * @param <T>
 * @param <TWRAPPER>
 */
public interface BulkReadListService<T, TWRAPPER> {

	List<T> getAll(String apiPath, Integer startIndex, Integer pageSize, String sortBy, String filter,
			String responseFields, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	Collection<T> getAll(String apiPath, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	List<T> findBy(String apiPath, String filter, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	List<T> getSubAll(String apiPath, String parentId, Integer startIndex, Integer pageSize, String sortBy,
			String filter, String responseFields, ParameterizedTypeReference<List<T>> responseType) throws Exception;

	Collection<T> getSubAll(String apiPath, String parentId, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

	List<T> findSubBy(String apiPath, String parentId, String filter, ParameterizedTypeReference<List<T>> responseType)
			throws Exception;

}
