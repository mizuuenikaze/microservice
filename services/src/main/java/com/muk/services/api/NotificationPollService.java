package com.muk.services.api;

import java.time.ZonedDateTime;

public interface NotificationPollService {
	boolean isPollNeeded();

	ZonedDateTime getLastSuccessfulNotificationUtc() throws Exception;

	void updateLastSuccessfulNotifcationUtc(long timestamp) throws Exception;
}
