package com.muk.services.csv;

import java.util.List;
import java.util.Map;

public interface CsvDocumentCache {
	boolean addDocument(String filename, List<String> columnNames);

	Map<String, String> mergeMap(String key, List<String> columnValues);

}
