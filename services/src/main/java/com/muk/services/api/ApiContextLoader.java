package com.muk.services.api;

import java.util.Map;

import org.apache.camel.Exchange;

import com.muk.ext.core.api.Dummy;

public interface ApiContextLoader {
	public static final String X_VOL_TENANT = "x-vol-tenant";
	public static final String X_VOL_SITE = "x-vol-site";
	public static final String X_VOL_CATALOG = "x-vol-catalog";
	public static final String X_VOL_SITEGROUP = "x-vol-site-group";
	public static final String X_VOL_MASTER_CATALOG = "x-vol-master-catalog";
	public static final String X_VOL_SITE_DOMAIN = "x-vol-site-domain";
	public static final String X_VOL_TENANT_DOMAIN = "x-vol-tenant-domain";
	public static final String X_VOL_CORRELATION = "x-vol-correlation";
	public static final String X_VOL_HMAC_SHA256 = "x-vol-hmac-sha256";
	public static final String X_VOL_APP_CLAIMS = "x-vol-app-claims";
	public static final String X_VOL_USER_CLAIMS = "x-vol-user-claims";
	public static final String X_VOL_VERSION = "x-vol-version";
	public static final String X_VOL_DATAVIEW_MODE = "x-vol-dataview-mode";
	public static final String X_VOL_LOCALE = "x-vol-locale";
	public static final String X_VOL_CURRENCY = "x-vol-currency";
	public static final String DATE = "Date";
	public static final String ETAG = "ETag";

	// supports the muk sdk usage only
	Dummy getApiContext();

	/**
	 * Store on the local thread context.
	 *
	 * @param headers
	 */
	void storeLocalApiContext(Map<String, String> headers);

	/**
	 * Store on the camel exchange message.
	 *
	 * @param Exchange
	 */
	void storeLocalApiContext(Map<String, String> headers, Exchange exchange);

	/**
	 * Store an externally generated context locally.
	 *
	 * @param externalContext
	 */
	void storeLocalApiContext(Dummy externalContext);
}
