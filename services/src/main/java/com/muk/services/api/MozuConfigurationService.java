package com.muk.services.api;

public interface MozuConfigurationService {
	static final String MASTERCATALOG = "muk.config.masterCatalogId";
	static final String BASEURL = "muk.config.baseUrl";
	static final String APPID = "muk.config.appId";
	static final String TENANTUSERNAME = "muk.config.tenantUsername";
	static final String PASSWORD = "muk.config.password";
	static final String MZDB_QUEUE = "muk.db.queue";
	static final String MZDB_NAMESPACE = "muk.db.namespace";
	static final String MZDB_NOTIFICATION_TIME = "muk.db.notificationTime";

	Integer getMasterCatalogId();

	String getBaseUrl();

	String getPciUrl();

	String getAppId();

	String getTenantUserName();

	String getPassword();

	String getMzdbQueue();

	String getMzdbNamespace();

	String getMzdbNotificationTime();
}
