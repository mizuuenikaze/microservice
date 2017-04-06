package com.muk.services.web.client;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.HttpClientConnectionManager;

public class IdleConnectionMonitor implements Runnable {
	private final HttpClientConnectionManager connMgr;

	public IdleConnectionMonitor(HttpClientConnectionManager connMgr) {
		this.connMgr = connMgr;
	}

	@Override
	public void run() {
		try {
			while (!Thread.currentThread().isInterrupted()) {
				Thread.sleep(5000);
				connMgr.closeExpiredConnections();
				connMgr.closeIdleConnections(30, TimeUnit.SECONDS);
			}
		} catch (final InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
	}
}
