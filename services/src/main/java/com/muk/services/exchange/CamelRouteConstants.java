/*******************************************************************************
 * Copyright (C)  2017  mizuuenikaze inc
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
		public static final String actionId = "mukApiActionId";
		public static final String mukEventId = "MukEventId";
		public static final String queueDestination = "CamelJmsDestination";
	}
}
