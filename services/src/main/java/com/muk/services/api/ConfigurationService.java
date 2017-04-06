package com.muk.services.api;

public interface ConfigurationService {

	static final String INVENTORY_UPDATE = "muk.nrt.interval";
	static final String MEDIUM_FREQUENCY_PERIOD = "muk.medium.interval";
	static final String SFTP_TARGET = "muk.sftp.target";

	String getNearRealTimeInterval();

	String getMediumInterval();
	
	String getSftpTarget();
}
