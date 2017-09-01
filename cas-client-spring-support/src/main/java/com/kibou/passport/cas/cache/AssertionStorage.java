package com.kibou.passport.cas.cache;

import org.jasig.cas.client.validation.Assertion;

public interface AssertionStorage {// key-value cache

	public Assertion get(String key);

	public void put(String key, Assertion assertion);
}
