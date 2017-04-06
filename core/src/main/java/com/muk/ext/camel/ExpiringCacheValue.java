package com.muk.ext.camel;

public class ExpiringCacheValue<V> {

	private long inserted;
	private V value;

	public ExpiringCacheValue() {

	}

	public ExpiringCacheValue(V value) {
		this.value = value;
	}

	public boolean isExpired(long expiration) {
		return System.currentTimeMillis() > (inserted + expiration);
	}

	public long getInserted() {
		return inserted;
	}

	public void setInserted(long inserted) {
		this.inserted = inserted;
	}

	public V getValue() {
		return value;
	}

	public void setValue(V value) {
		this.value = value;
	}
}
