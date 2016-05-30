package com.bzsoft.harrison.proto;



public class ProtocolRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	/**
	 * Zero-arg constructor.
	 */
	public ProtocolRuntimeException() {
	}

	/**
	 * Create the exception.
	 *
	 * @param message
	 *            the message
	 */
	public ProtocolRuntimeException(final String message) {
		super(message);
	}

	/**
	 * Create the exception.
	 *
	 * @param message
	 *            the message
	 * @param rootCause
	 *            the root cause
	 */
	public ProtocolRuntimeException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * Create the exception.
	 *
	 * @param rootCause
	 *            the root cause
	 */
	public ProtocolRuntimeException(final Throwable rootCause) {
		super(rootCause);
	}

}