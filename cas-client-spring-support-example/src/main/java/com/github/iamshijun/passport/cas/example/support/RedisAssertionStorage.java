package com.github.iamshijun.passport.cas.example.support;

import javax.annotation.Resource;

import org.jasig.cas.client.validation.Assertion;
import org.springframework.data.redis.core.HashOperations;

import com.github.iamshijun.passport.cas.cache.AssertionStorage;

/**
 * @author aimysaber@gmail.com
 *
 */
public class RedisAssertionStorage implements AssertionStorage{
	
	@Resource(name = "redisTemplate")
	private HashOperations<String, String, Assertion> hashOperation;
	
	private String cacheKey = "cas_assertion";
	
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	@Override
	public Assertion get(String key) {
		return hashOperation.get(cacheKey, key);
	}

	@Override
	public void put(String key, Assertion value) {
		hashOperation.put(cacheKey, key, value);
	}

}
