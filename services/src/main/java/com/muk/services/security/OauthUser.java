package com.muk.services.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import com.muk.ext.core.ProjectCoreVersion;

public class OauthUser extends User {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	private String refreshToken;
	private final String openIdToken;
	private String secondaryToken;

	public OauthUser(String username, String password, Collection<? extends GrantedAuthority> authorities,
			String refreshToken, String openIdToken) {
		this(username, password, true, true, true, true, authorities, refreshToken, openIdToken);
	}

	public OauthUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities,
			String refreshToken, String openIdToken) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.refreshToken = refreshToken;
		this.openIdToken = openIdToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getOpenIdToken() {
		return openIdToken;
	}

	public String getSecondaryToken() {
		return secondaryToken;
	}

	public void setSecondaryToken(String secondaryToken) {
		this.secondaryToken = secondaryToken;
	}
}
