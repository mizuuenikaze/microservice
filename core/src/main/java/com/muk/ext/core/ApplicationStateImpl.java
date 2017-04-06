package com.muk.ext.core;

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import com.muk.ext.core.json.model.ApplicationSettings;

/**
 * Maintains tenant state based on event notifications over the entire
 * application.
 *
 */
public class ApplicationStateImpl implements ApplicationState {

	private ConcurrentHashMap<Integer, TenantResource> applicationResourceMap;

	public ApplicationStateImpl() {
		applicationResourceMap = new ConcurrentHashMap<Integer, TenantResource>();
	}

	@Override
	public void close() {
		for (TenantResource resource : applicationResourceMap.values()) {
			resource.close();
		}

		applicationResourceMap.clear();
	}

	@Override
	public TenantResource addTenant(Integer tenantId, Integer siteId) {
		return getResource(tenantId, siteId);
	}

	@Override
	public TenantResource removeTenant(Integer tenantId, Integer siteId) {
		TenantResource resource = applicationResourceMap.remove(tenantId);
		resource.close();
		return resource;
	}

	@Override
	public boolean exists(Integer tenantId) {
		return applicationResourceMap.containsKey(tenantId);
	}

	@Override
	public Iterator<TenantResource> getIterator() {
		return applicationResourceMap.values().iterator();
	}

	protected TenantResource getResource(Integer tenantId, Integer siteId) {
		TenantResource resource = applicationResourceMap.get(tenantId);

		if (resource == null) {
			ApplicationSettings setting = new ApplicationSettings(); // get
																		// settings
																		// from
																		// mzdb
			resource = applicationResourceMap.putIfAbsent(tenantId, createResource(tenantId, siteId, setting));
		} else {
			resource.addSite(siteId);
		}

		return resource;
	}

	protected TenantResource createResource(Integer tenantId, Integer siteId, ApplicationSettings setting) {
		TenantResource resource = new TenantResource(tenantId, siteId, setting);

		return resource;
	}

}
