package com.kibou.passport.cas.cache;

public interface TicketStorage {// key-value cache

	public String get(String key);

	public void put(String key, String value);
}
