package com.github.iamshijun.passport.cas.cache;

import org.jasig.cas.client.validation.Assertion;

/**
 * Assertion Storage Interface
 * @author aimysaber@gmail.com
 *
 */
public interface AssertionStorage {// key-value cache

	public Assertion get(String key);

	public void put(String key, Assertion assertion);
}
