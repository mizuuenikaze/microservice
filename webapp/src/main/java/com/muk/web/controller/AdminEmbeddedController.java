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
package com.muk.web.controller;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
