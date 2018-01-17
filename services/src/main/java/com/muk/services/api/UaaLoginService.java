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

import java.util.Map;

import org.springframework.web.util.UriComponents;

/**
 *
 * Describes a simplified login server rest api.
 * <p>
 * This allows for a login api to Uaa that does not contain any ui. Great for SPA clients.
 */
public interface UaaLoginService {
	Map<String, Object> loginForClient(String username, String password, String clientId,
			UriComponents inUrlComponents);

	String approveClient(String approvalQuery, String cookie);

}
