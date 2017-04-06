package com.muk.services.api;

import java.util.Map;

public interface CsvImportService {
	void translateAndSave(String fileName, Map<String, String> record) throws Exception;
}
