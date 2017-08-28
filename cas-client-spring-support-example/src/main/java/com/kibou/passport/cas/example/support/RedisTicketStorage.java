package com.kibou.passport.cas.example.support;

import javax.annotation.Resource;

import org.springframework.data.redis.core.HashOperations;

import com.kibou.passport.cas.cache.TicketStorage;

//@Component("redisTicketStorage")
public class RedisTicketStorage implements TicketStorage{

	@Resource(name = "redisTemplate")
	private HashOperations<String, String, String> hashOperation;
	
	private String cacheKey = "cas_ticket";
	
	public void setCacheKey(String cacheKey) {
		this.cacheKey = cacheKey;
	}
	
	@Override
	public String get(String key) {
		return hashOperation.get(cacheKey, key);
	}

	@Override
	public void put(String key, String value) {
		hashOperation.put(cacheKey, key, value);
	}

}
