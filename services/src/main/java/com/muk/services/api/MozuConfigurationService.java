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
