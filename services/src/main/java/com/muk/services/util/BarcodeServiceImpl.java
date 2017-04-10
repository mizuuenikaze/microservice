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
package com.muk.services.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.exec.CommandLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.muk.ext.process.ProcessExecutor;
import com.muk.services.api.BarcodeService;

/**
 * Invokes imagemagick to generate the barcode png
 * 
 */
public class BarcodeServiceImpl implements BarcodeService {
	private static final Logger LOG = LoggerFactory.getLogger(BarcodeServiceImpl.class);

	@Override
	public File generateBarcode(File path, String giftCardId) {

		CommandLine cmdLine = null;

		if (System.getProperty("os.name").equals("Linux")) {
			cmdLine = new CommandLine("convert");
		} else {
			cmdLine = new CommandLine("convert.exe");
		}

		cmdLine.addArgument("-font");
		cmdLine.addArgument("Free-3-of-9-Extended-Regular");
		cmdLine.addArgument("-pointsize");
		cmdLine.addArgument("40");
		cmdLine.addArgument("-bordercolor");
		cmdLine.addArgument("white");
		cmdLine.addArgument("-border");
		cmdLine.addArgument("10x10");
		cmdLine.addArgument("label:${giftCardId}");
		cmdLine.addArgument("${outfile}");

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("giftCardId", giftCardId);
		map.put("outfile", path);

		cmdLine.setSubstitutionMap(map);

		ProcessExecutor executor = new ProcessExecutor();

		try {
			if (0 == executor.runCommandLine(cmdLine, 100000)) {
				return path;
			}
		} catch (IOException ex) {
			LOG.error("Failed to execute imagemagick", ex);
		}

		return null;

	}
}
