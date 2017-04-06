package com.muk.ext.camel;

import java.util.Map;

import org.apache.camel.api.management.ManagedAttribute;
import org.apache.camel.api.management.ManagedOperation;
import org.apache.camel.spi.IdempotentRepository;
import org.apache.camel.support.ServiceSupport;

public class ExpiringIdempotentRepository extends ServiceSupport implements IdempotentRepository<String> {
	private Map<String, ExpiringCacheValue<Object>> cache;
	private int cacheSize;
	private long expiration;

	public ExpiringIdempotentRepository() {
		this.cache = new ExpiringLRUCache<String, Object>(1000, 10000);
	}

	public ExpiringIdempotentRepository(Map<String, ExpiringCacheValue<Object>> set) {
		this.cache = set;
	}

	/**
	 * Creates a new memory based repository using a {@link ExpiringLRUCache}
	 * with a default of 1000 entries in the cache and default 10 second ttl.
	 */
	public static IdempotentRepository<String> expiringIdempotentRepository() {
		return new ExpiringIdempotentRepository();
	}

	/**
	 * Creates a new memory based repository using a {@link ExpiringLRUCache}.
	 *
	 * @param cacheSize
	 *            the cache size
	 * @param timeToLive
	 *            the max duration in the cache
	 */
	public static IdempotentRepository<String> expiringIdempotentRepository(int cacheSize, long timeToLive) {
		return expiringIdempotentRepository(new ExpiringLRUCache<String, Object>(cacheSize, timeToLive));
	}

	/**
	 * Creates a new memory based repository using the given {@link Map} to use
	 * to store the processed message ids.
	 * <p/>
	 * Care should be taken to use a suitable underlying {@link Map} to avoid
	 * this class being a memory leak.
	 *
	 * @param cache
	 *            the cache
	 */
	public static IdempotentRepository<String> expiringIdempotentRepository(
			Map<String, ExpiringCacheValue<Object>> cache) {
		return new ExpiringIdempotentRepository(cache);
	}

	@Override
	@ManagedOperation(description = "Adds the key to the store")
	public boolean add(String key) {
		synchronized (cache) {
			if (cache.containsKey(key)) {
				return false;
			} else {
				cache.put(key, new ExpiringCacheValue<Object>(key));
				return true;
			}
		}
	}

	@Override
	@ManagedOperation(description = "Does the store contain the given key")
	public boolean contains(String key) {
		synchronized (cache) {
			return cache.containsKey(key);
		}
	}

	@Override
	@ManagedOperation(description = "Remove the key from the store")
	public boolean remove(String key) {
		synchronized (cache) {
			return cache.remove(key) != null;
		}
	}

	@Override
	public boolean confirm(String key) {
		// noop
		return true;
	}

	@Override
	@ManagedOperation(description = "Clear the store")
	public void clear() {
		synchronized (cache) {
			cache.clear();
		}
	}

	public Map<String, ExpiringCacheValue<Object>> getCache() {
		return cache;
	}

	@ManagedAttribute(description = "The current cache size")
	public int getCacheSize() {
		return cache.size();
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	@ManagedAttribute(description = "The current cache element max time to live")
	public long getExpiration() {
		return expiration;
	}

	public void setExpiration(long expiration) {
		this.expiration = expiration;
	}

	@Override
	protected void doStart() throws Exception {
		if (cacheSize > 0) {
			cache = new ExpiringLRUCache<String, Object>(cacheSize, expiration);
		}
	}

	@Override
	protected void doStop() throws Exception {
		cache.clear();
	}
}
