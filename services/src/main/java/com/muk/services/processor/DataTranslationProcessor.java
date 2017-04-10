package com.muk.services.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.processor.AbstractInboundProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.csv.CsvRecord;
import com.muk.ext.status.Status;
import com.muk.services.api.CsvImportService;
import com.muk.services.exchange.CsvImportStatus;

/**
 *
 * Routes a csv data record to the appropriate service
 *
 */
public class DataTranslationProcessor extends AbstractInboundProcessor<CsvRecord, CsvImportStatus> {
	private static final Pattern tenantPattern = Pattern.compile("_t(\\d+)");

	@Inject
	@Qualifier("mukCsvImportService")
	private CsvImportService csvImportService;


	@Override
	protected CsvImportStatus forceFail(Exchange exchange) {
		final CsvImportStatus status = successStatus();
		status.setStatus(Status.ERROR);
		status.setMessage("Force fail.");
		return status;
	}

	@Override
	protected Class<? extends CsvRecord> getBodyClass() {
		return CsvRecord.class;
	}

	@Override
	protected CsvImportStatus handleExchange(CsvRecord body, Exchange exchange) throws Exception {
		final CsvImportStatus status = successStatus();
		status.setRecord(body.getRecord());

		final Map<String, String> headers = new HashMap<String, String>();
		final String csvFile = exchange.getIn().getHeader(Exchange.FILE_NAME, String.class);
		final Matcher matcher = tenantPattern.matcher(csvFile);
		matcher.find();



		try {
			csvImportService.translateAndSave(csvFile, body.getRecord());
		} catch (final Exception e) {
			status.setCause(e);
			status.setStatus(Status.ERROR);
			status.setMessage("Failed to translate and save csv record.");
		}

		return status;
	}

	@Override
	protected boolean propagateHeaders() {
		return true;
	}

	@Override
	protected boolean propagateAttachments() {
		return true;
	}

	@Inject
	@Qualifier("processCsvImportBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<CsvImportStatus> beanGenerator) {
		super.setBeanGenerator(beanGenerator);
	}

}
