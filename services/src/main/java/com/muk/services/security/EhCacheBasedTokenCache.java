package com.muk.services.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.cache.EhCacheBasedUserCache;
import org.springframework.util.Assert;

/**
 * Caches <code>User</code> objects using a Spring IoC defined
 * <A HREF="http://ehcache.sourceforge.net">EHCACHE</a>.
 *
 * @see org.springframework.security.core.userdetails.cache.
 *      EhCacheBasedUserCache
 */
public class EhCacheBasedTokenCache implements UserCache, InitializingBean {
	private static final Log logger = LogFactory.getLog(EhCacheBasedUserCache.class);

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
		final UserDetails element = (UserDetails) cache.get(key).get();

		if (logger.isDebugEnabled()) {
			logger.debug("Cache hit: " + (element != null) + "; username: " + key);
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

			if (logger.isDebugEnabled()) {
				logger.debug("Cache put: " + ((OauthUser) user).getSecondaryToken());
			}

			cache.put(((OauthUser) user).getSecondaryToken(), user);
		}
	}

	public void removeUserFromCache(UserDetails user) {

		if (!(user instanceof OauthUser)) {
			throw new UnsupportedOperationException("User is not an OauthUser type.");
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Cache remove: " + user.getUsername());
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
