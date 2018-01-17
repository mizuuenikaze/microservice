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

import java.util.Collection;

import org.springframework.util.MultiValueMap;

public interface BulkReadService<T, TWRAPPER> {

	TWRAPPER getAll(String apiPath, Integer startIndex, Integer pageSize, String sortBy, String filter,
			String responseFields, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER getAll(String apiPath, MultiValueMap<String, String> parameters, Class<TWRAPPER> responseType)
			throws Exception;

	Collection<T> getAll(String apiPath, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER findBy(String apiPath, String filter, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER getSubAll(String apiPath, String parentId, Integer startIndex, Integer pageSize, String sortBy,
			String filter, String responseFields, Class<TWRAPPER> responseType) throws Exception;

	Collection<T> getSubAll(String apiPath, String parentId, Class<TWRAPPER> responseType) throws Exception;

	TWRAPPER findSubBy(String apiPath, String parentId, String filter, Class<TWRAPPER> responseType) throws Exception;

}
