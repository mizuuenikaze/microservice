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
package com.muk.services.csv;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * Holds an association of file to csv column headers.
 *
 */
public class DefaultCsvDocumentCache implements CsvDocumentCache {
	private Map<String, List<String>> documentCache;

	@Override
	public boolean addDocument(String filename, List<String> columnNames) {
		return null == documentCache.put(filename, columnNames);
	}

	@Override
	public Map<String, String> mergeMap(String key, List<String> columnValues) {
		final List<String> columns = documentCache.get(key);
		final Map<String, String> row = new LinkedHashMap<String, String>();

		final Iterator<String> column = columns.iterator();
		final Iterator<String> value = columnValues.iterator();

		// Add a marker for the file that originated the row
		row.put("file", key);

		do {
			row.put(column.next(), value.next());
		} while (column.hasNext());

		return row;
	}

	public void setDocumentCache(Map<String, List<String>> documentCache) {
		this.documentCache = documentCache;
	}
}
