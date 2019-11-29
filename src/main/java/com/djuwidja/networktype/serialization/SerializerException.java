/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.djuwidja.networktype.serialization;
/**
 * Exception thrown by {@link Serializer}.
 * @author kennethdjuwidja
 * @since 1.0.0
 * @version 1.0.0
 */
public class SerializerException extends Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SerializerException() {
        super();
    }

    public SerializerException(String message) {
        super(message);
    }

    public SerializerException(String message, Throwable cause) {
        super(message, cause);
    }

    public SerializerException(Throwable cause) {
        super(cause);
    }
}
