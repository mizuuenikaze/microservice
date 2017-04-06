package com.muk.services.strategy;

import java.util.Map;

public interface ImportStrategy<T> {
	void importData(Map<String, String> record, T objectToImport) throws Exception;

}
