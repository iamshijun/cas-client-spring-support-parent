package com.kibou.passport.serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.kibou.passport.exception.SerializationException;

/**
 * @author aimysaber@gmail.com
 *
 */
public class JdkObjectSerializer implements ObjectSerializer<Object> {

	@Override
	public byte[] serialize(Object t) throws SerializationException {
		try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream oos = new ObjectOutputStream(bos);) {

			oos.writeObject(t);

			return bos.toByteArray();
		} catch (IOException e) {
			throw new SerializationException("Cannot serialize", e);
		}
	}

	@Override
	public Object deserialize(byte[] bytes) throws SerializationException {
		try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
			
			return ois.readObject();
			
		} catch (IOException | ClassNotFoundException e) {
			throw new SerializationException("Cannot deserialize", e);
		}
	}

}
