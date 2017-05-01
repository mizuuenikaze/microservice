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
		if (getPrincipal() instanceof OauthUser) {
			return ((OauthUser) getPrincipal()).getUsername();
		}

		return super.getName();
	}

	@Override
	public Object getCredentials() {
		if (getPrincipal() instanceof OauthUser) {
			return ((OauthUser)getPrincipal()).getSecondaryToken();
		}

		return " ";
	}

	@Override
	public Object getPrincipal() {
		return principal;
	}
}
