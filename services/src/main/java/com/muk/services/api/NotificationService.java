package com.muk.services.api;

import java.time.ZonedDateTime;
import java.util.List;

import com.muk.services.api.model.ExtendedEvent;

public interface NotificationService {
	List<ExtendedEvent> fetchNotifications(ZonedDateTime nextPoll) throws Exception;

	long lastPolledTime();

}
