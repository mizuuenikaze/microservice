package com.muk.services.api;

public interface AdminService {
	/**
	 * Enable the application on the tenant.
	 */
	void enable() throws Exception;

	/**
	 * Setup attributes on the tenant
	 */
	void initializeAttributes() throws Exception;

	void initializeDocuments() throws Exception;

	void generalSetup() throws Exception;

}
