package com.muk.ext.camel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.util.LRUCache;

import com.muk.ext.core.ProjectCoreVersion;

public class ExpiringLRUCache<K, V> extends LRUCache<K, ExpiringCacheValue<V>> {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	private Long expiration;

	public ExpiringLRUCache(int maximumCacheSize, long timeToLive) {
		this(maximumCacheSize, maximumCacheSize, timeToLive);
	}

	public ExpiringLRUCache(int initialCapacity, int maximumCacheSize, long timeToLive) {
		super(initialCapacity, maximumCacheSize);
		this.expiration = timeToLive;
	}

	public ExpiringLRUCache(int initialCapacity, int maximumCacheSize, boolean stopOnEviction, long timeToLive) {
		super(initialCapacity, maximumCacheSize, stopOnEviction);
		this.expiration = timeToLive;
	}

	@Override
	public boolean containsKey(Object o) {
		evictExpiredValues();
		return super.containsKey(o);
	}

	@Override
	public ExpiringCacheValue<V> get(Object o) {
		evictExpiredValues();
		return super.get(o);
	}

	@Override
	public ExpiringCacheValue<V> put(K k, ExpiringCacheValue<V> v) {
		v.setInserted(System.currentTimeMillis());
		return super.put(k, v);
	}

	private void evictExpiredValues() {
		// remove expired entries
		final List<K> removeList = new ArrayList<K>();

		final Iterator<Entry<K, ExpiringCacheValue<V>>> iterator = entrySet().iterator();

		while (iterator.hasNext()) {
			final Entry<K, ExpiringCacheValue<V>> entry = iterator.next();

			if (entry.getValue().isExpired(expiration)) {
				removeList.add(entry.getKey());
			}
		}

		for (final K evictee : removeList) {
			remove(evictee);
		}
	}
}
