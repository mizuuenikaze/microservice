/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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
package com.muk.services.strategy.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.muk.ext.core.json.model.ActionDoc;
import com.muk.ext.core.json.model.AppointmentRequest;
import com.muk.ext.core.json.model.CalendarEvent;
import com.muk.ext.core.json.model.CalendarEventAttendee;
import com.muk.ext.core.json.model.CalendarEventTime;
import com.muk.services.api.ExternalOauthService;
import com.muk.services.api.SecurityConfigurationService;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.strategy.ActionStrategy;

/**
 * Uses the google calendar api to request an event on a public calendar.
 *
 */
public class SchedulerService implements ActionStrategy {
	private static final Logger LOG = LoggerFactory.getLogger(SchedulerService.class);
	private static final String ZONEINFO_UTC = "UTC";
	private static final String CALENDAR_URL = "https://www.googleapis.com/calendar/v3/calendars/winduponthewater.com_7jugibjb4imd36m00ivfuadvgc@group.calendar.google.com/events";
	private static final String CALENDAR_LIST = "https://www.googleapis.com/calendar/v3/users/me/calendarList";

	@Inject
	@Qualifier("securityConfigurationService")
	private SecurityConfigurationService secConfigService;

	@Inject
	private ExternalOauthService googleOauthService;

	@Inject
	@Qualifier("genericRestTemplate")
	private RestTemplate restTemplate;

	@Override
	public void performAction(ActionDoc doc) {

		final String accessToken = googleOauthService.authorizeRequest(ExternalOauthService.SCOPE_CALENDAR,
				secConfigService.getCalendarAlias(), secConfigService.getCalendarAccount());

		doc.setStatus(ServiceConstants.SimpleStates.error);

		try {
			//listCalendars(accessToken);
			requestEvent(doc, accessToken);
		} catch (final HttpClientErrorException httpClientEx) {
			LOG.error("Failed to request calendar event.", httpClientEx);
			doc.setMessage("Status: " + httpClientEx.getStatusCode().value() + " - " + httpClientEx.getMessage());
		}
	}

	private void requestEvent(ActionDoc doc, String accessToken) {
		final AppointmentRequest appointment = (AppointmentRequest) doc.getPayload();
		final CalendarEvent event = new CalendarEvent();
		final List<CalendarEventAttendee> attendees = new ArrayList<CalendarEventAttendee>(2);
		attendees.add(new CalendarEventAttendee());
		attendees.add(new CalendarEventAttendee());

		attendees.get(0).setEmail(secConfigService.getPrimaryEmail());
		attendees.get(0).setResponseStatus("tentative");
		attendees.get(1).setEmail(appointment.getWho());

		event.setAttendees(attendees);
		event.setColorId("5");
		event.setGuestCanInviteOthers(Boolean.FALSE);
		event.setGuestCanModify(Boolean.FALSE);
		event.setLocation(appointment.getWhere());
		event.setStatus("tentative");
		event.setSummary(appointment.getWhat());
		event.setDescription(appointment.getWhy());

		final CalendarEventTime end = new CalendarEventTime(), start = new CalendarEventTime();
		start.setDateTime(appointment.getStart());
		start.setTimeZone(ZONEINFO_UTC);
		end.setDateTime(appointment.getEnd());
		end.setTimeZone(ZONEINFO_UTC);
		event.setStart(start);
		event.setEnd(end);

		final HttpEntity<CalendarEvent> request = createRequestEntity(event, accessToken);
		final ResponseEntity<CalendarEvent> response = restTemplate.postForEntity(CALENDAR_URL, request,
				CalendarEvent.class);

		if (response.hasBody()) {
			doc.setStatus(ServiceConstants.SimpleStates.success);
		} else {
			doc.setMessage("No body in calendar event response.");
		}
	}

	@SuppressWarnings("unused")
	private ResponseEntity<JsonNode> listCalendars(String accessToken) {
		final HttpEntity<Object> request = createRequestEntity((Object) null, accessToken);
		final ResponseEntity<JsonNode> response = restTemplate.exchange(CALENDAR_LIST, HttpMethod.GET, request,
				JsonNode.class);
		return response;
	}

	private <T> HttpEntity<T> createRequestEntity(T payload, String accessToken) {
		final HttpHeaders headers = new HttpHeaders();

		headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
		headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);

		if (payload != null) {
			headers.setContentType(MediaType.APPLICATION_JSON);
		}

		return new HttpEntity<T>(payload, headers);
	}
}
