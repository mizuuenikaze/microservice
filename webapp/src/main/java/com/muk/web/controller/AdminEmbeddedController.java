package com.muk.web.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.muk.services.api.LogReaderService;
import com.muk.web.WebConstants;

/**
 * This controller is not secure under spring security because of how the mozu
 * admin embeds this in an iframe as a post.
 *
 */
@Controller
@RequestMapping(value = "/embed")
public class AdminEmbeddedController {
	private static final Logger LOG = LoggerFactory.getLogger(AdminEmbeddedController.class);

	@Inject
	private DefaultViewController safeController;

	@Inject
	@Qualifier("errorLogReaderService")
	private LogReaderService errorLogReaderService;

	@RequestMapping(value = "/splash", method = RequestMethod.POST)
	public String getSplash(Model model) {

		final String view = safeController.getSplash(model);

		return view;
	}

	@RequestMapping(value = "/config", method = RequestMethod.POST)
	public String enterConfig() {
		return WebConstants.Views.config;
	}
}
