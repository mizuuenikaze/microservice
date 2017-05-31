package com.muk.restlet;

import java.net.HttpCookie;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.camel.Exchange;
import org.apache.camel.TypeConverter;
import org.apache.camel.component.restlet.DefaultRestletBinding;
import org.apache.camel.util.ObjectHelper;
import org.restlet.Response;
import org.restlet.data.CookieSetting;
import org.restlet.data.Method;
import org.restlet.engine.header.HeaderConstants;
import org.restlet.util.Series;

public class CustomRestletBinding extends DefaultRestletBinding {

	@Override
	protected boolean setResponseHeader(Exchange exchange, Response message, String header, Object value) {
		if (!super.setResponseHeader(exchange, message, header, value)) {
			// other custom stuff like set-cookie...
			if (message.getEntity() != null) {
				if (header.equalsIgnoreCase(HeaderConstants.HEADER_SET_COOKIE)) {
					final Series<CookieSetting> series = convertToSeries(value);
					message.setCookieSettings(series);
					return true;
				}
				if (header.equalsIgnoreCase(HeaderConstants.HEADER_ACCESS_CONTROL_ALLOW_CREDENTIALS)) {
					final Boolean bool = exchange.getContext().getTypeConverter().tryConvertTo(Boolean.class, value);
					if (bool != null) {
						message.setAccessControlAllowCredentials(bool);
					}
					return true;
				}
				if (header.equalsIgnoreCase(HeaderConstants.HEADER_ACCESS_CONTROL_ALLOW_METHODS)) {
					final Set<Method> set = convertToMethodSet(value, exchange.getContext().getTypeConverter());
					message.setAccessControlAllowMethods(set);
					return true;

				}
			}
		}

		return false;
	}

	private Series<CookieSetting> convertToSeries(Object value) {
		List<HttpCookie> httpCookies = null;
		final Series<CookieSetting> series = new Series<CookieSetting>(CookieSetting.class);
		@SuppressWarnings("rawtypes")
		final Iterator it = ObjectHelper.createIterator(value);
		while (it.hasNext()) {
			final Object next = it.next();

			if (next instanceof String) {
				httpCookies = HttpCookie.parse((String) next);

				for (final HttpCookie cookie : httpCookies) {
					final CookieSetting cookieSetting = new CookieSetting(cookie.getVersion(), cookie.getName(),
							cookie.getValue(), cookie.getPath(), cookie.getDomain());
					series.add(cookieSetting);
				}
			}
		}
		return series;
	}

	@SuppressWarnings("unchecked")
	private Set<Method> convertToMethodSet(Object value, TypeConverter typeConverter) {
		if (value instanceof Set) {
			return (Set<Method>) value;
		}
		final Set<Method> set = new LinkedHashSet<>();
		final Iterator it = ObjectHelper.createIterator(value);
		while (it.hasNext()) {
			final Object next = it.next();
			final String text = typeConverter.tryConvertTo(String.class, next);
			if (text != null) {
				final Method method = Method.valueOf(text.trim()); // creates new instance only if no matching instance exists
				set.add(method);
			}
		}
		return set;
	}
}
