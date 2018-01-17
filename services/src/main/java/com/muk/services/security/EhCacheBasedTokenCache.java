/*******************************************************************************
 * Copyright (C)  2018  mizuuenikaze inc
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.EhCacheBasedUserCache;
import org.springframework.util.Assert;

/**
 * Caches <code>User</code> objects using a Spring IoC defined <A HREF="http://ehcache.sourceforge.net">EHCACHE</a>.
 *
 * @see org.springframework.security.core.userdetails.cache. EhCacheBasedUserCache
 */
public class EhCacheBasedTokenCache implements UserCache, InitializingBean {
	private static final Logger LOG = LoggerFactory.getLogger(EhCacheBasedUserCache.class);

	private Cache cache;

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(cache, "cache mandatory");
	}

	public Cache getCache() {
		return cache;
	}

	@Override
	public UserDetails getUserFromCache(String key) {
		final UserDetails element = cache.get(key) != null ? (UserDetails) cache.get(key).get() : null;

		if (LOG.isDebugEnabled()) {
			LOG.debug("Cache hit: " + (element != null) + "; username: " + key);
		}

		if (element == null) {
			return null;
		} else {
			return element;
		}
	}

	@Override
	public void putUserInCache(UserDetails user) {
		if (user != null) {
			if (!(user instanceof OauthUser)) {
				throw new UnsupportedOperationException("User is not an OauthUser type.");
			}

			if (LOG.isDebugEnabled()) {
				LOG.debug("Cache put: " + ((OauthUser) user).getSecondaryToken());
			}

			cache.put(((OauthUser) user).getSecondaryToken(), user);
		}
	}

	public void removeUserFromCache(UserDetails user) {

		if (!(user instanceof OauthUser)) {
			throw new UnsupportedOperationException("User is not an OauthUser type.");
		}

		if (LOG.isDebugEnabled()) {
			LOG.debug("Cache remove: " + user.getUsername());
		}

		this.removeUserFromCache(((OauthUser) user).getSecondaryToken());
	}

	@Override
	public void removeUserFromCache(String key) {
		cache.evict(key);
	}

	public void setCache(Cache cache) {
		this.cache = cache;
	}

}
