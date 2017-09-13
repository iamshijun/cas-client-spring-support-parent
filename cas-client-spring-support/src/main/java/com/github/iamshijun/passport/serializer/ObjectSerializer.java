package com.github.iamshijun.passport.serializer;

import com.github.iamshijun.passport.exception.SerializationException;

/**
 * @author aimysaber@gmail.com
 *
 * @param <T>
 */
public interface ObjectSerializer<T> {

	byte[] serialize(T t) throws SerializationException;

	T deserialize(byte[] bytes) throws SerializationException;
}