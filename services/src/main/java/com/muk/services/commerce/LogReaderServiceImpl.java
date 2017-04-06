package com.muk.services.commerce;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import com.muk.ext.core.file.SeekingLogFileReader;
import com.muk.services.api.LogReaderService;

public class LogReaderServiceImpl implements LogReaderService {

	@Override
	public List<String> readFromDate(String logFile, ZonedDateTime logDate) {
		final List<String> errors = new ArrayList<String>();

		SeekingLogFileReader reader = new SeekingLogFileReader();
		Stream<String> lines = null;

		try {
			reader.open(logFile);
			lines = reader.read(logDate);

			lines.forEach(new Consumer<String>() {

				@Override
				public void accept(String t) {
					errors.add(t);
				}

			});
		} catch (IOException ioEx) {
			errors.add("Failed to read logFile: " + logFile);
			errors.add(ioEx.getMessage());
		} finally {
			reader.close(lines);
		}

		return errors;

	}
}
