package com.muk.services.exchange;

public interface NotificationEvent {
	interface Keys {
		public static final String mukEventId = "MozuEventId";
		public static final String queueDestination = "CamelJmsDestination";
	}
}
