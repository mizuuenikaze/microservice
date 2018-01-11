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
package com.muk.ext.core.json.model;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Calendar json representation.
 *
 * @link https://developers.google.com/google-apps/calendar/v3/reference/events#resource
 *
 */
@JsonInclude(Include.NON_NULL)
public class CalendarEvent {
	private String kind;
	private String etag;
	private String id;
	private String status;
	private String htmlLink;
	private LocalDateTime created;
	private LocalDateTime updated;
	private String summary;
	private String description;
	private String location;
	private String colorId;
	private CalendarEventPerson creator;
	private CalendarEventPerson organizer;
	private CalendarEventTime start;
	private CalendarEventTime end;
	private Boolean endTimeUnspecified;
	private List<String> recurrence;
	private String recurringEventId;
	private CalendarEventTime originalStartTime;
	private String transparency;
	private String visibility;
	private String iCalUID;
	private Integer sequence;
	private List<CalendarEventAttendee> attendees;
	private Boolean attendeesOmitted;
	private CalendarEventProperties extendedProperties;
	private String hangoutLink;
	private CalendarEventConference conferenceData;
	private Boolean anyoneCanAddSelf;
	private Boolean guestCanInviteOthers;
	private Boolean guestCanModify;
	private Boolean guestCanSeeOtherGuests;
	private Boolean privateCopy;
	private Boolean locked;
	private CalendarEventReminder reminders;
	private CalendarEventSource source;

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getEtag() {
		return etag;
	}

	public void setEtag(String etag) {
		this.etag = etag;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHtmlLink() {
		return htmlLink;
	}

	public void setHtmlLink(String htmlLink) {
		this.htmlLink = htmlLink;
	}

	public LocalDateTime getCreated() {
		return created;
	}

	public void setCreated(LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}

	public void setUpdated(LocalDateTime updated) {
		this.updated = updated;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getColorId() {
		return colorId;
	}

	public void setColorId(String colorId) {
		this.colorId = colorId;
	}

	public CalendarEventPerson getCreator() {
		return creator;
	}

	public void setCreator(CalendarEventPerson creator) {
		this.creator = creator;
	}

	public CalendarEventPerson getOrganizer() {
		return organizer;
	}

	public void setOrganizer(CalendarEventPerson organizer) {
		this.organizer = organizer;
	}

	public CalendarEventTime getStart() {
		return start;
	}

	public void setStart(CalendarEventTime start) {
		this.start = start;
	}

	public CalendarEventTime getEnd() {
		return end;
	}

	public void setEnd(CalendarEventTime end) {
		this.end = end;
	}

	public Boolean getEndTimeUnspecified() {
		return endTimeUnspecified;
	}

	public void setEndTimeUnspecified(Boolean endTimeUnspecified) {
		this.endTimeUnspecified = endTimeUnspecified;
	}

	public List<String> getRecurrence() {
		return recurrence;
	}

	public void setRecurrence(List<String> recurrence) {
		this.recurrence = recurrence;
	}

	public String getRecurringEventId() {
		return recurringEventId;
	}

	public void setRecurringEventId(String recurringEventId) {
		this.recurringEventId = recurringEventId;
	}

	public CalendarEventTime getOriginalStartTime() {
		return originalStartTime;
	}

	public void setOriginalStartTime(CalendarEventTime originalStartTime) {
		this.originalStartTime = originalStartTime;
	}

	public String getTransparency() {
		return transparency;
	}

	public void setTransparency(String transparency) {
		this.transparency = transparency;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public String getiCalUID() {
		return iCalUID;
	}

	public void setiCalUID(String iCalUID) {
		this.iCalUID = iCalUID;
	}

	public Integer getSequence() {
		return sequence;
	}

	public void setSequence(Integer sequence) {
		this.sequence = sequence;
	}

	public List<CalendarEventAttendee> getAttendees() {
		return attendees;
	}

	public void setAttendees(List<CalendarEventAttendee> attendees) {
		this.attendees = attendees;
	}

	public Boolean getAttendeesOmitted() {
		return attendeesOmitted;
	}

	public void setAttendeesOmitted(Boolean attendeesOmitted) {
		this.attendeesOmitted = attendeesOmitted;
	}

	public CalendarEventProperties getExtendedProperties() {
		return extendedProperties;
	}

	public void setExtendedProperties(CalendarEventProperties extendedProperties) {
		this.extendedProperties = extendedProperties;
	}

	public String getHangoutLink() {
		return hangoutLink;
	}

	public void setHangoutLink(String hangoutLink) {
		this.hangoutLink = hangoutLink;
	}

	public Boolean getAnyoneCanAddSelf() {
		return anyoneCanAddSelf;
	}

	public void setAnyoneCanAddSelf(Boolean anyoneCanAddSelf) {
		this.anyoneCanAddSelf = anyoneCanAddSelf;
	}

	public Boolean getGuestCanInviteOthers() {
		return guestCanInviteOthers;
	}

	public void setGuestCanInviteOthers(Boolean guestCanInviteOthers) {
		this.guestCanInviteOthers = guestCanInviteOthers;
	}

	public Boolean getGuestCanModify() {
		return guestCanModify;
	}

	public void setGuestCanModify(Boolean guestCanModify) {
		this.guestCanModify = guestCanModify;
	}

	public Boolean getGuestCanSeeOtherGuests() {
		return guestCanSeeOtherGuests;
	}

	public void setGuestCanSeeOtherGuests(Boolean guestCanSeeOtherGuests) {
		this.guestCanSeeOtherGuests = guestCanSeeOtherGuests;
	}

	public Boolean getPrivateCopy() {
		return privateCopy;
	}

	public void setPrivateCopy(Boolean privateCopy) {
		this.privateCopy = privateCopy;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public CalendarEventReminder getReminders() {
		return reminders;
	}

	public void setReminders(CalendarEventReminder reminders) {
		this.reminders = reminders;
	}

	public CalendarEventSource getSource() {
		return source;
	}

	public void setSource(CalendarEventSource source) {
		this.source = source;
	}

	public CalendarEventConference getConferenceData() {
		return conferenceData;
	}

	public void setConferenceData(CalendarEventConference conferenceData) {
		this.conferenceData = conferenceData;
	}
}
