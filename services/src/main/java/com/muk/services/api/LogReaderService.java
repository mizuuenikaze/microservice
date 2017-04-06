package com.muk.services.api;

import java.time.ZonedDateTime;
import java.util.List;

public interface LogReaderService {
	List<String> readFromDate(String logFile, ZonedDateTime logDate);
}
