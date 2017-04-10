package com.muk.services.exchange;

public interface NotificationEvent {
	interface Keys {
		public static final String mukEventId = "MukEventId";
		public static final String queueDestination = "CamelJmsDestination";
	}
}
