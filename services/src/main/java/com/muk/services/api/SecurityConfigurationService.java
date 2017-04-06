package com.muk.services.api;

public interface SecurityConfigurationService {

	static final String OAUTH_CLIENT_ID = "oauth.clientId";
	static final String OAUTH_SERVER = "oauth.server";
	static final String OAUTH_TOKEN_PATH = "oauth.tokenPath";
	static final String OAUTH_USERINFO_PATH = "oauth.userInfoPath";
	static final String OAUTH_CHECKTOKEN_PATH = "oauth.checkTokenPath";
	static final String OAUTH_SALT = "oauth.salt";

	String getOauthServer();

	String getOauthServiceClientId();

	String getOauthTokenPath();

	String getOauthUserInfoPath();

	String getOauthCheckTokenPath();

	String getSalt();
}
