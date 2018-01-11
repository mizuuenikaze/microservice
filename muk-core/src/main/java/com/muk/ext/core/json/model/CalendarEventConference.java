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

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_NULL)
public class CalendarEventConference {
	private CalendarEventConferenceRequest createRequest;
	private CalendarEventConferenceStatus status;
	private List<CalendarEventConferenceEntryPoint> entryPoints;
	private CalendarEventConferenceSolution conferenceSolution;
	private String conferenceId;
	private String signature;

	public CalendarEventConferenceRequest getCreateRequest() {
		return createRequest;
	}

	public void setCreateRequest(CalendarEventConferenceRequest createRequest) {
		this.createRequest = createRequest;
	}

	public CalendarEventConferenceStatus getStatus() {
		return status;
	}

	public void setStatus(CalendarEventConferenceStatus status) {
		this.status = status;
	}

	public List<CalendarEventConferenceEntryPoint> getEntryPoints() {
		return entryPoints;
	}

	public void setEntryPoints(List<CalendarEventConferenceEntryPoint> entryPoints) {
		this.entryPoints = entryPoints;
	}

	public CalendarEventConferenceSolution getConferenceSolution() {
		return conferenceSolution;
	}

	public void setConferenceSolution(CalendarEventConferenceSolution conferenceSolution) {
		this.conferenceSolution = conferenceSolution;
	}

	public String getConferenceId() {
		return conferenceId;
	}

	public void setConferenceId(String conferenceId) {
		this.conferenceId = conferenceId;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
}
