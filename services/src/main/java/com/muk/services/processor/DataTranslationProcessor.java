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
package com.muk.services.processor;

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
