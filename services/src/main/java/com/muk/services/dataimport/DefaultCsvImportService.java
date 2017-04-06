package com.muk.services.dataimport;

import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.core.api.Dummy;
import com.muk.services.api.CsvImportService;
import com.muk.services.exchange.ServiceConstants;
import com.muk.services.strategy.ImportStrategy;
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

	@Inject
	@Qualifier("customerAccountImportStrategy")
	private ImportStrategy<Dummy> customerAccountImportStrategy;


	@Override
	public void translateAndSave(String fileName, Map<String, String> record) throws Exception {
		switch (fileName.substring(0, fileName.indexOf("_t"))) {
		case ServiceConstants.ImportFiles.purge:
			customerAccountImportStrategy.importData(record, translate(ServiceConstants.ImportFiles.purge, record));
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
