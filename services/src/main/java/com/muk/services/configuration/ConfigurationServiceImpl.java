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
package com.muk.services.configuration;

import com.muk.services.api.ProjectConfigurator;

/**
 *
 * Aggregate of all properties file configurations.
 *
 */
public class ConfigurationServiceImpl implements ProjectConfigurator {

	private String nearRealTimeInterval;
	private String mediumInterval;
	private String sftpTarget;
	private String oauthServer;
	private String oauthServiceClientId;
	private String oauthTokenPath;
	private String oauthUserInfoPath;
	private String oauthCheckTokenPath;
	private String payPalClientId;
	private String stripeClientId;
	private String payPalUri;
	private String stripeUri;
	private String couchDbUri;
	private String salt;

	@Override
	public String getNearRealTimeInterval() {
		return nearRealTimeInterval;
	}

	public void setNearRealTimeInterval(String nearRealTimeInterval) {
		this.nearRealTimeInterval = nearRealTimeInterval;
	}

	@Override
	public String getMediumInterval() {
		return mediumInterval;
	}

	public void setMediumInterval(String mediumInterval) {
		this.mediumInterval = mediumInterval;
	}

	@Override
	public String getOauthServer() {
		return oauthServer;
	}

	public void setOauthServer(String oauthServer) {
		this.oauthServer = oauthServer;
	}

	@Override
	public String getOauthServiceClientId() {
		return oauthServiceClientId;
	}

	public void setOauthServiceClientId(String oauthServiceClientId) {
		this.oauthServiceClientId = oauthServiceClientId;
	}

	@Override
	public String getOauthTokenPath() {
		return oauthTokenPath;
	}

	public void setOauthTokenPath(String oauthTokenPath) {
		this.oauthTokenPath = oauthTokenPath;
	}

	@Override
	public String getOauthUserInfoPath() {
		return oauthUserInfoPath;
	}

	public void setOauthUserInfoPath(String oauthUserInfoPath) {
		this.oauthUserInfoPath = oauthUserInfoPath;
	}

	@Override
	public String getOauthCheckTokenPath() {
		return oauthCheckTokenPath;
	}

	public void setOauthCheckTokenPath(String oauthCheckTokenPath) {
		this.oauthCheckTokenPath = oauthCheckTokenPath;
	}

	@Override
	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	@Override
	public String getSftpTarget() {
		return sftpTarget;
	}

	public void setSftpTarget(String sftpTarget) {
		this.sftpTarget = sftpTarget;
	}

	@Override
	public String getPayPalClientId() {
		return payPalClientId;
	}

	public void setPayPalClientId(String payPalClientId) {
		this.payPalClientId = payPalClientId;
	}

	@Override
	public String getPayPalUri() {
		return payPalUri;
	}

	public void setPayPalUri(String payPalUri) {
		this.payPalUri = payPalUri;
	}

	@Override
	public String getStripeClientId() {
		return stripeClientId;
	}

	public void setStripeClientId(String stripeClientId) {
		this.stripeClientId = stripeClientId;
	}

	@Override
	public String getStripeUri() {
		return stripeUri;
	}

	public void setStripeUri(String stripeUri) {
		this.stripeUri = stripeUri;
	}

	@Override
	public String getCouchDbUri() {
		return couchDbUri;
	}

	public void setCouchDbUri(String couchDbUri) {
		this.couchDbUri = couchDbUri;
	}
}
