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
package com.muk.ext.core.file;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Searches Log file for a date and streams.
 * 
 * 
 */
public class SeekingLogFileReader {
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm:ss,SSSXXX");
	private Stream<String> lines;
	private boolean accepted;

	public void open(String file) throws IOException {

		Path logPath = Paths.get(file);
		lines = Files.lines(logPath);
	}

	public Stream<String> read(final ZonedDateTime startingTimestamp) {

		return lines.filter(new Predicate<String>() {

			@Override
			public boolean test(String t) {
				if (t.indexOf("--") > 0) {
					accepted = (startingTimestamp.compareTo(ZonedDateTime.parse(
							t.substring(0, t.indexOf("--") - 1) + ZonedDateTime.now().getOffset().getId(),
							formatter)) <= 0);
				} else if (t.indexOf("Error") > 0) {
					accepted &= t.startsWith("com.muk");
				} else {
					accepted = false;
				}

				return accepted;
			}

		}).limit(100);
	}

	public void close(Stream<String> lines) {
		if (lines != null) {
			lines.close();
		}
	}
}
