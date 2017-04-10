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
