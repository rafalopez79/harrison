package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class OutputClientStreamEmptyIterable<T extends Serializable> implements StreamIterable<T> {


	public OutputClientStreamEmptyIterable() {
		//empty
	}

	@Override
	public void close() throws IOException {
		//empty
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<T>() {
			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public T next() {
				throw new NoSuchElementException();
			}

			@Override
			public void remove(){
				throw new UnsupportedOperationException("remove");
			}
		};
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
