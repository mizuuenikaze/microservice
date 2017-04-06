package com.muk.services.exchange;

public interface CamelRouteConstants {
	interface RouteIds {
		public static final String sftpFileFetch = "sftpFileFetch";
		public static final String csvFileParse = "csvFileParse";
		public static final String nearRealTime = "nearRealTime";
		public static final String mediumPeriodic = "mediumPeriodic";
		public static final String notificationPoll = "notificationPoll";
		public static final String asyncNotificationPush = "asyncNotificationPush";
	}

	interface MessageHeaders {
		public static final String mukApiContext = "mukapicontext";
	}
}
