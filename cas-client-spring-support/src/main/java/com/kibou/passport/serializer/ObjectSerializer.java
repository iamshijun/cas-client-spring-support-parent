package com.kibou.passport.serializer;

import com.kibou.passport.exception.SerializationException;

public interface ObjectSerializer<T> {

	byte[] serialize(T t) throws SerializationException;

	T deserialize(byte[] bytes) throws SerializationException;
}