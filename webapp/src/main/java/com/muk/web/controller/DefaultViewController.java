package com.muk.web.controller;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.muk.services.api.LogReaderService;
import com.muk.web.WebConstants;

@Controller
@RequestMapping(value = "/ext")
public class DefaultViewController {

	@Inject
	@Qualifier("errorLogReaderService")
	private LogReaderService errorLogReaderService;

	@RequestMapping(value = "/errorLog", method = RequestMethod.GET)
	public String getErrors(@RequestParam(value = "dateFormat", required = false) String dateFormat,
			@RequestParam(value = "date", required = false) String date, Model model) throws IOException {

		List<String> errors = null;

		if (dateFormat == null ^ date == null) {
			errors = new ArrayList<String>();
			errors.add("Both dateFormat and date must be provided or absent.");
			model.addAttribute("logErrors", errors);
			return WebConstants.Views.errorLog;
		} else {
			if (dateFormat == null && date == null) {
				// defaults
				dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX'['VV']'";
				date = ZonedDateTime.now().minusMinutes(10l).toString();
			}
		}

		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateFormat);
		final ZonedDateTime startingTimestamp = ZonedDateTime.parse(date, formatter);
		errors = errorLogReaderService.readFromDate(System.getProperty("custom.logging.root") + "/error.log",
				startingTimestamp);

		model.addAttribute("logErrors", errors);
		return WebConstants.Views.errorLog;
	}

	@RequestMapping(value = "/splash", method = RequestMethod.GET)
	public String getSplash(Model model) {

		return WebConstants.Views.splash;
	}

	@RequestMapping(value = "/spa", method = RequestMethod.GET)
	public String enterSpa() {
		return WebConstants.Views.spa;
	}

	@RequestMapping(value = "/config", method = RequestMethod.GET)
	public String enterConfig() {
		return WebConstants.Views.config;
	}
}
