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

import java.lang.management.ManagementFactory;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.management.MBeanServerConnection;
import javax.management.ObjectName;

import org.apache.camel.Exchange;
import org.restlet.data.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

import com.muk.ext.camel.processor.AbstractRestProcessor;
import com.muk.ext.core.AbstractBeanGenerator;
import com.muk.ext.core.json.RestReply;
import com.muk.services.json.RouteAction;

public class RouteActionProcessor extends AbstractRestProcessor<RouteAction, RestReply> {
	private static final Logger LOG = LoggerFactory.getLogger(RouteActionProcessor.class);

	private MBeanServerConnection server;

	@Override
	protected Class<RouteAction> getBodyClass() {
		return RouteAction.class;
	}

	@Override
	protected RestReply handleExchange(RouteAction body, Exchange exchange) throws Exception {

		switch (body.getAction()) {
		case START:
			jmxInvoke("start", body.getRouteId());
			break;
		case STOP:
			jmxInvoke("stop", body.getRouteId());
			break;
		default:
			// nop;
		}

		exchange.getOut().setHeader(Exchange.HTTP_RESPONSE_CODE, Integer.valueOf(Status.SUCCESS_CREATED.getCode()));
		RestReply restReply = createResponse();

		restReply.setMessage("Action succeeded");

		return restReply;
	}

	private void jmxInvoke(String operation, String routeId) throws Exception {
		ObjectName objName = new ObjectName("org.apache.camel:type=routes,*");
		List<ObjectName> cacheList = new LinkedList<ObjectName>(server.queryNames(objName, null));
		for (Iterator<ObjectName> iter = cacheList.iterator(); iter.hasNext();) {
			objName = iter.next();
			String keyProps = objName.getCanonicalKeyPropertyListString();
			if (keyProps.contains(routeId)) {
				ObjectName objectRouteName = new ObjectName("org.apache.camel:" + keyProps);
				Object[] params = {};
				String[] sig = {};
				server.invoke(objectRouteName, operation, params, sig);
				return;
			}
		}
	}

	@Override
	protected RestReply forceFail(Exchange exchange) {
		RestReply reply = createResponse();
		reply.setMessage("Force fail.");

		return reply;
	}

	@Inject
	@Qualifier("restBeanGenerator")
	@Override
	public void setBeanGenerator(AbstractBeanGenerator<RestReply> beanGenerator) {
		super.setBeanGenerator(beanGenerator);

	}

	@PostConstruct
	public void postConstruct() {
		server = ManagementFactory.getPlatformMBeanServer();
	}
}
