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
package com.muk.ext.camel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.camel.util.LRUCache;

import com.muk.ext.core.ProjectCoreVersion;

/**
 * A least recently used cached with time to live expiration.
 */
public class ExpiringLRUCache<K, V> extends LRUCache<K, ExpiringCacheValue<V>> {
	private static final long serialVersionUID = ProjectCoreVersion.SERIAL_VERSION_UID;

	/**
	 * Expiration in milliseconds.
	 */
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
		// remove expired entries based on ttl
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
