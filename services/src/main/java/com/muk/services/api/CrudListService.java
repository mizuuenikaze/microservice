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
