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
package com.muk.ext.core;

import java.util.HashSet;
import java.util.Set;

import com.muk.ext.core.json.model.ApplicationSettings;

public class TenantResource {
	private Integer tenantId;
	private Set<Integer> siteIds;

	public TenantResource() {
		siteIds = new HashSet<Integer>();
	}

	public TenantResource(Integer tenantId, Integer siteId, ApplicationSettings setting) {
		this();

		this.tenantId = tenantId;
		siteIds.add(siteId);
	}

	public void close() {
		siteIds.clear();
	}

	public void addSite(Integer siteId) {
		siteIds.add(siteId);
	}

	public Integer getTenantId() {
		return tenantId;
	}

	public void setTenantId(Integer tenantId) {
		this.tenantId = tenantId;
	}

	public Set<Integer> getSiteIds() {
		return siteIds;
	}

	public void setSiteIds(Set<Integer> siteIds) {
		this.siteIds = siteIds;
	}
}
