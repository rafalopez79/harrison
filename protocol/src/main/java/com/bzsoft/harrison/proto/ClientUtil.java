package com.bzsoft.harrison.proto;

import java.io.Serializable;
import java.util.Iterator;

public final class ClientUtil {

	private ClientUtil(){
		//empty
	}

	public static <T extends Serializable> StreamIterable<T> createStreamIterable(final Iterable<T> iterable){
		return new InputClientStreamIterable<T>(iterable);
	}

	public static <T extends Serializable> StreamIterable<T> createStreamIterable(final Iterator<T> iterator){
		return new InputClientStreamIterable<T>(iterator);
	}
}
