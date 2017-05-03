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
package com.muk.spring.config;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.apache.camel.ExchangePattern;
import org.apache.camel.LoggingLevel;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.dataformat.csv.CsvDataFormat;
import org.apache.camel.spring.SpringRouteBuilder;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.ExpiringIdempotentRepository;
import com.muk.ext.csv.CsvRecord;
import com.muk.services.api.ProjectConfigurator;
import com.muk.services.api.model.ExtendedEvent;
import com.muk.services.exchange.CamelRouteConstants;
import com.muk.services.exchange.NotificationEvent;
import com.muk.services.exchange.ServiceConstants;

/**
 *
 * Camel route configuration.
 *
 */
public class Router extends SpringRouteBuilder {

	@Inject
	@Qualifier("cfgService")
	private ProjectConfigurator configurationService;

	@Override
	public void configure() throws Exception {

		final Map<String, JacksonDataFormat> jacksonJMSFormats = new HashMap<String, JacksonDataFormat>();

		jacksonJMSFormats.put("mukEvent", new JacksonDataFormat(ExtendedEvent.class));
		jacksonJMSFormats.get("mukEvent").setAllowJmsType(true);
		jacksonJMSFormats.get("mukEvent").setModuleClassNames("com.fasterxml.jackson.datatype.jsr310.JavaTimeModule");

		jacksonJMSFormats.put("csvRecord", new JacksonDataFormat(CsvRecord.class));
		jacksonJMSFormats.get("csvRecord").setAllowJmsType(true);

		// notification handling
		from("direct:intent").bean("queueDemux", "routeToQueue")
		.idempotentConsumer(header(NotificationEvent.Keys.mukEventId),
				ExpiringIdempotentRepository.expiringIdempotentRepository(200, 20000l))
		.marshal(jacksonJMSFormats.get("mukEvent")).to(ExchangePattern.InOnly, "activemq:queue:dummy");

		from("activemq:queue:unknown?asyncConsumer=false").log(LoggingLevel.DEBUG,
				org.slf4j.LoggerFactory.getLogger("com.muk.spring.config.Router"),
				"Unknown Event Received >>>>> ${body}");

		// csv handling
		final CsvDataFormat csvFormat = new CsvDataFormat();
		csvFormat.setLazyLoad(false);
		csvFormat.setRecordSeparator("\n");
		csvFormat.setSkipHeaderRecord(false);

		fromF("%s?maxMessagesPerPoll=%d&delay=%s&initialDelay=%s&moveFailed=.error&doneFileName=${file:name.noext}.done",
				configurationService.getSftpTarget(), 1, "1m", "30s").routeId(CamelRouteConstants.RouteIds.csvFileParse)
		.split(body().tokenize("\n")).streaming().unmarshal(csvFormat).bean("csvDemux", "routeToQueue")
		.marshal(jacksonJMSFormats.get("csvRecord")).to(ExchangePattern.InOnly, "activemq:queue:dummy");


		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueAppInstalled + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("mukEvent"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueAppInstalled);
		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueAppUninstalled + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("mukEvent"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueAppUninstalled);
		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueAppEnabled + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("mukEvent"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueAppEnabled);
		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueAppDisabled + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("mukEvent"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueAppDisabled);
		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueAppUpgraded + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("mukEvent"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueAppUpgraded);

		// data transformation
		from("activemq:queue:" + ServiceConstants.QueueDestinations.queueCsvRow + "?asyncConsumer=false")
		.unmarshal(jacksonJMSFormats.get("csvRecord"))
		.to("direct:" + ServiceConstants.QueueDestinations.queueCsvRow);

		from("direct:" + ServiceConstants.QueueDestinations.queueAppInstalled).process("nopProcessor")
		.bean("statusHandler", "logProcessStatus");
		from("direct:" + ServiceConstants.QueueDestinations.queueAppUninstalled).process("nopProcessor")
		.bean("statusHandler", "logProcessStatus");
		from("direct:" + ServiceConstants.QueueDestinations.queueAppEnabled).process("nopProcessor")
		.bean("statusHandler", "logProcessStatus");
		from("direct:" + ServiceConstants.QueueDestinations.queueAppDisabled).process("nopProcessor")
		.bean("statusHandler", "logProcessStatus");
		from("direct:" + ServiceConstants.QueueDestinations.queueAppUpgraded).process("nopProcessor")
		.bean("statusHandler", "logProcessStatus");

		// data transformation
		from("direct:" + ServiceConstants.QueueDestinations.queueCsvRow).process("dataTranslationProcessor")
		.bean("statusHandler", "logProcessStatus").choice().when()
		.simple("${body.status} == ${type:com.muk.ext.status.Status.ERROR}").transform()
		.simple("${body.record}").marshal(csvFormat)
		.toF("%s?fileName=processErrors.log&fileExist=Append", configurationService.getSftpTarget());
	}
}
