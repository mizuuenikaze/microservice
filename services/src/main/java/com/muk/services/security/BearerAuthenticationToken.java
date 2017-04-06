package com.muk.services.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import com.muk.ext.core.ProjectCoreVersion;

public class BearerAuthenticationToken extends AbstractAuthenticationToken {

	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	private final Object principal;

	public BearerAuthenticationToken(Object principal) {
		super(null);
		this.principal = principal;
		setAuthenticated(false);
	}

	public BearerAuthenticationToken(Object principal, Collection<? extends GrantedAuthority> authorities) {
		super(authorities);
		this.principal = principal;
		setAuthenticated(true);
	}

	@Override
	public String getName() {
		if (this.getPrincipal() instanceof OauthUser) {
			return ((OauthUser) this.getPrincipal()).getUsername();
		}

		return super.getName();
	}

	@Override
	public Object getCredentials() {
		return " ";
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
