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
package com.muk.services.exchange;

public interface ServiceConstants {
	interface Keys {
		public static final String camelFlatpackCounter = "camelFlatpackCounter";
	}

	interface Codes {
		public static final String eventCatOrder = "order";
		public static final String eventCatApplication = "application";
		public static final String openedEvent = "opened";
		public static final String disabledEvent = "disabled";
		public static final String enabledEvent = "enabled";
		public static final String installedEvent = "installed";
		public static final String uninstalledEvent = "uninstalled";
		public static final String upgradedEvent = "upgraded";
	}

	interface QueueDestinations {
		public static final String queueAppInstalled = "mukAppInstalledEvent";
		public static final String queueAppUninstalled = "mukAppUninstalledEvent";
		public static final String queueAppEnabled = "mukAppEnabledEvent";
		public static final String queueAppDisabled = "mukAppDisabledEvent";
		public static final String queueAppUpgraded = "mukAppUpgradedEvent";
		public static final String queueCsvRow = "csvDataRow";
	}

	interface ImportFiles {
		public static final String purge = "Purge";
	}

	interface CacheNames {
		public static final String userCache = "ehOauthUserCache";
	}
}
