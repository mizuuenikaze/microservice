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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CalendarEventAttendee extends CalendarEventPerson {
	private Boolean organizer;
	private Boolean resource;
	private Boolean optional;
	private String responseStatus;
	private String comment;
	private Integer additionalGuests;

	public Boolean getOrganizer() {
		return organizer;
	}

	public void setOrganizer(Boolean organizer) {
		this.organizer = organizer;
	}

	public Boolean getResource() {
		return resource;
	}

	public void setResource(Boolean resource) {
		this.resource = resource;
	}

	public Boolean getOptional() {
		return optional;
	}

	public void setOptional(Boolean optional) {
		this.optional = optional;
	}

	public String getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(String responseStatus) {
		this.responseStatus = responseStatus;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Integer getAdditionalGuests() {
		return additionalGuests;
	}

	public void setAdditionalGuests(Integer additionalGuests) {
		this.additionalGuests = additionalGuests;
	}
}
