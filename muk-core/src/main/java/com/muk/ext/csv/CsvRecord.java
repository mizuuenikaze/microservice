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
package com.muk.ext.csv;

import java.io.Serializable;
import java.util.Map;

import com.muk.ext.core.ProjectCoreVersion;

/**
 * Holds one csv record with associated header information
 *
 */
public class CsvRecord implements Serializable {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	private Map<String, String> record;

	public CsvRecord() {
	};

	public CsvRecord(Map<String, String> record) {
		this.record = record;
	}

	public Map<String, String> getRecord() {
		return record;
	}

	public void setRecord(Map<String, String> record) {
		this.record = record;
	}
}