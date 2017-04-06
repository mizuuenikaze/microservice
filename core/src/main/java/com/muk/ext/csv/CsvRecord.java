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