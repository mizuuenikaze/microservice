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
