package com.bzsoft.harrison.proto;

import java.io.Serializable;
import java.util.Iterator;

class InputClientStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private final Iterable<T> iterable;

	protected InputClientStreamIterable(final Iterator<T> iterator) {
		this.iterable = IterableIterator.of(iterator);
	}

	protected InputClientStreamIterable(final Iterable<T> iterable) {
		this.iterable = iterable;
	}

	@Override
	public void close() {
		// empty
	}

	@Override
	public Iterator<T> iterator() {
		if (iterable != null) {
			return iterable.iterator();
		}
		return null;
	}

	@Override
	public void cancel() {
		// empty
	}

	@Override
	public void write(final T item)  {
		throw new UnsupportedOperationException();
	}

}
