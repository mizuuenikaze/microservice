package com.muk.ext.core;

import java.util.Iterator;

public interface ApplicationState {
	void close();

	TenantResource addTenant(Integer tenantId, Integer siteId);

	TenantResource removeTenant(Integer tenantId, Integer siteId);

	boolean exists(Integer tenantId);

	Iterator<TenantResource> getIterator();
}
