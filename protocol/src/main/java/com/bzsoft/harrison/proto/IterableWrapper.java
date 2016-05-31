package com.bzsoft.harrison.proto;

import java.util.Iterator;

public class IterableWrapper<T> implements Iterable<T> {

	private static class IteratorWrapper<T> implements Iterator<T>{

		private final Iterator<T> iterator;

		private IteratorWrapper(final Iterator<T> iterator) {
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			try{
				return iterator.hasNext();
			}catch(final Exception e){
				throw new IteratorException(e);
			}
		}

		@Override
		public T next() {
			try{
				return iterator.next();
			}catch(final Exception e){
				throw new IteratorException(e);
			}
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException();
		}
	}

	private final Iterable<T> iterable;

	public static <T> Iterable<T> of(final Iterable<T> iterable){
		return new IterableWrapper<T>(iterable);
	}

	private IterableWrapper(final Iterable<T> iterable) {
		this.iterable = iterable;
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorWrapper<T>(iterable.iterator());
	}

}
