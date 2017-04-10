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
package com.muk.services.dataimport;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.services.api.CsvImportService;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.strategy.TranslationFactoryStrategy;
import com.muk.services.strategy.TranslationStrategy;

/**
 * Delegates the import logic of a csv records to an appropriate strategy or
 * service.
 *
 */
public class DefaultCsvImportService implements CsvImportService {
	private static final Logger LOG = LoggerFactory.getLogger(DefaultCsvImportService.class);

	@Inject
	@Qualifier("translationFactoryStrategy")
	private TranslationFactoryStrategy translationFactoryStrategy;

	//	@Inject
	//	@Qualifier("customerAccountImportStrategy")
	//	private ImportStrategy<Dummy> customerAccountImportStrategy;


	@Override
	public void translateAndSave(String fileName, Map<String, String> record) throws Exception {
		switch (fileName.substring(0, fileName.indexOf("_t"))) {
		case ServiceConstants.ImportFiles.purge:
			//customerAccountImportStrategy.importData(record, translate(ServiceConstants.ImportFiles.purge, record));
			break;
		default:
			LOG.info("Unknown file to import.  " + fileName);
		}
	}

	private <T> T translate(String key, Map<String, String> record) {
		final TranslationStrategy<Map<String, String>, T> translationStrategy = translationFactoryStrategy
				.findTranslationStrategy(key);
		return translationStrategy.translate(record);
	}
}
