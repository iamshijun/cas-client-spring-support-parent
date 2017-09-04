package com.kibou.passport.exception;

/**
 * @author aimysaber@gmail.com
 * 
 */
public class SerializationException extends RuntimeException {

	private static final long serialVersionUID = -3350203529687602242L;

	public SerializationException(String msg, Throwable cause) {
		super(msg, cause);
	}

	public SerializationException(String msg) {
		super(msg);
	}
}