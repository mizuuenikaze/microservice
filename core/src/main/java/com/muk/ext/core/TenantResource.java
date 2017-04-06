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
