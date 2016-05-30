package com.bzsoft.harrison.proto;

import java.io.IOException;

public class ProtocolException extends IOException {

	private static final long	serialVersionUID	= 1L;

	/**
	 * Zero-arg constructor.
	 */
	public ProtocolException() {
		// empty
	}

	/**
	 * Create the exception.
	 *
	 * @param message
	 *           the message
	 */
	public ProtocolException(final String message) {
		super(message);
	}

	/**
	 * Create the exception.
	 *
	 * @param message
	 *           the message
	 * @param rootCause
	 *           the root cause
	 */
	public ProtocolException(final String message, final Throwable rootCause) {
		super(message, rootCause);
	}

	/**
	 * Create the exception.
	 *
	 * @param rootCause
	 *           the root cause
	 */
	public ProtocolException(final Throwable rootCause) {
		super(rootCause);
	}

}