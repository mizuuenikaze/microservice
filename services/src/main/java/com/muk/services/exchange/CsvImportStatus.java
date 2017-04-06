package com.muk.services.exchange;

import java.util.Map;

public class CsvImportStatus extends ServiceProcessStatus {
	private Map<String, String> record;

	public Map<String, String> getRecord() {
		return record;
	}

	public void setRecord(Map<String, String> record) {
		this.record = record;
	}
}
