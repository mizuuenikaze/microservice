package com.muk.services.api;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.muk.ext.core.json.model.oauth.TokenResponse;

public interface CachingOauthUserDetailsService extends UserDetailsService {
	TokenResponse loadByAuthorizationCode(String authorizationCode);
}
