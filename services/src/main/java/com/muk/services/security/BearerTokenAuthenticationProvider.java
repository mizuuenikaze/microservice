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

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * Authenticates a bearer token against an oauth2 service provider.
 *
 */
public class BearerTokenAuthenticationProvider implements AuthenticationProvider {
	private static final Logger LOG = LoggerFactory.getLogger(BearerTokenAuthenticationProvider.class);

	protected boolean hideUserNotFoundExceptions = true;
	private final UserDetailsChecker postAuthenticationChecks = new DefaultPostAuthenticationChecks();

	@Inject
	@Qualifier("oauthUserDetailService")
	private UserDetailsService userDetailsService;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		final String username = (authentication.getPrincipal() == null) ? "NONE_PROVIDED" : authentication.getName();

		UserDetails user = null;

		try {
			user = retrieveUser(username);
		} catch (final UsernameNotFoundException notFound) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("User '" + username + "' not found");
			}

			if (hideUserNotFoundExceptions) {
				throw new BadCredentialsException("Bad credentials");
			} else {
				throw notFound;
			}
		}

		Assert.notNull(user, "retrieveUser returned null - a violation of the interface contract");

		postAuthenticationChecks.check(user);

		final Object principalToReturn = user;

		return createSuccessAuthentication(principalToReturn, authentication, user);
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return (BearerAuthenticationToken.class.isAssignableFrom(authentication));
	}

	protected final UserDetails retrieveUser(String secondaryToken) throws AuthenticationException {
		UserDetails loadedUser;

		try {
			loadedUser = this.getUserDetailsService().loadUserByUsername(secondaryToken);
		} catch (final Exception repositoryProblem) {
			throw new InternalAuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
		}

		if (loadedUser == null) {
			throw new InternalAuthenticationServiceException(
					"UserDetailsService returned null, which is an interface contract violation");
		}
		return loadedUser;
	}

	public UserDetailsService getUserDetailsService() {
		return userDetailsService;
	}

	/**
	 * @see org.springframework.security.authentication.dao.
	 *      AbstractUserDetailsAuthenticationProvider#setHideUserNotFoundExceptions(boolean)
	 */
	public void setHideUserNotFoundExceptions(boolean hideUserNotFoundExceptions) {
		this.hideUserNotFoundExceptions = hideUserNotFoundExceptions;
	}

	protected Authentication createSuccessAuthentication(Object principal, Authentication authentication,
			UserDetails user) {
		final BearerAuthenticationToken result = new BearerAuthenticationToken(principal, user.getAuthorities());
		result.setDetails(authentication.getDetails());

		return result;
	}

	private class DefaultPostAuthenticationChecks implements UserDetailsChecker {
		@Override
		public void check(UserDetails user) {
			if (!user.isCredentialsNonExpired()) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("User account credentials have expired");
				}

				throw new CredentialsExpiredException("User credentials have expired");
			}
		}
	}
}
