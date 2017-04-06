package com.muk.services.processor;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.apache.camel.Exchange;
import org.apache.camel.component.jms.JmsConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.csv.CsvRecord;
import com.muk.services.api.QueueDemultiplexer;
import com.muk.services.csv.CsvDocumentCache;
import com.muk.services.exchange.ServiceConstants;

/**
 *
 * Determines the activemq queue to route too. Puts the event id in the header
 * for idempotent consumer.
 *
 */
public class CsvQueueDemultiplexerImpl implements QueueDemultiplexer {
	private static final Logger LOG = LoggerFactory.getLogger(CsvQueueDemultiplexerImpl.class);

	@Inject
	@Qualifier("csvDocumentCache")
	private CsvDocumentCache csvDocumentCache;

	@Override
	public void routeToQueue(Exchange exchange) {
		String destination = "unknown";
		final List<List<String>> data = (List<List<String>>) exchange.getIn().getBody();

		if (data.size() > 1) {
			throw new IllegalStateException("Unexpected number of csv records found.");
		}

		if (!data.isEmpty()) {
			final Integer index = exchange.getProperty("CamelSplitIndex", Integer.class);
			if (index != null && index.equals(0)) {
				final List<String> columnHeaders = new ArrayList<String>();
				for (final String column : data.get(0)) {
					columnHeaders.add(column);
				}
				csvDocumentCache.addDocument(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class),
						columnHeaders);

			} else {
				destination = ServiceConstants.QueueDestinations.queueCsvRow;
				exchange.getIn().setBody(new CsvRecord(csvDocumentCache
						.mergeMap(exchange.getIn().getHeader(Exchange.FILE_NAME, String.class), data.get(0))));
			}
		}

		exchange.getIn().setHeader(JmsConstants.JMS_DESTINATION, destination);
	}
}
