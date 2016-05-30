package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

class InputClientStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private final Iterable<T> iterable;

	protected InputClientStreamIterable(final Iterator<T> iterator) {
		this.iterable = new IterableIterator<T>(iterator);
	}

	protected InputClientStreamIterable(final Iterable<T> iterable) {
		this.iterable = iterable;
	}

	@Override
	public void close() throws IOException {
		//empty
	}

	@Override
	public Iterator<T> iterator() {
		if (iterable != null){
			return iterable.iterator();
		}
		return null;
	}

	@Override
	public void cancel() throws IOException {
		//empty
	}

	@Override
	public void write(final T item) throws IOException {
		throw new UnsupportedOperationException();
	}

}
