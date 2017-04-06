package com.muk.core.ext.test;

import static org.junit.Assert.assertTrue;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import org.junit.Test;

public class DateFormatTest {

	@Test
	public void parseTest() {
		final String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'";
		final String date = ZonedDateTime.now().minusMinutes(10l).toString();

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		final ZonedDateTime startingTimestamp = ZonedDateTime.parse(date, formatter);

		assertTrue(startingTimestamp != null);
	}

	@Test
	public void anotherParseTest() {
		final String dateFormat = "dd MMM yyyy HH:mm:ss,SSSXXX";
		String date = "11 Sep 2015 08:55:44,256";

		date = date + ZonedDateTime.now().getOffset().getId();

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		final ZonedDateTime startingTimestamp = ZonedDateTime.parse(date, formatter);

		assertTrue(startingTimestamp != null);
	}
}
