package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bzsoft.harrison.proto.stream.SerializableObjectInput;

public class InputServerStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private static final class StreamIterator<T> implements Iterator<T> {

		private final SerializableObjectInput soi;
		private Boolean next;

		private StreamIterator(final SerializableObjectInput soi) {
			this.soi = soi;
			this.next = null;
		}

		@Override
		public boolean hasNext() {
			try {
				if (next != null) {
					return next.booleanValue();
				}
				final boolean streamNext = soi.readBoolean();
				next = Boolean.valueOf(streamNext);
				if (!streamNext) {
					soi.readStreamEnd();
					final Throwable t = (Throwable) soi.readObject();
					if (t != null) {
						throw new IteratorException(t);
					}
				}
				return streamNext;
			} catch (final IOException e) {
				throw new ProtocolRuntimeException(e);
			} catch (final ClassNotFoundException ce) {
				throw new ProtocolRuntimeException(ce);
			}
		}

		@Override
		public T next() {
			try {
				if (next == null) {
					final boolean hasNext = hasNext();
					if (!hasNext) {
						throw new NoSuchElementException();
					}
				}
				@SuppressWarnings("unchecked")
				final T t = (T) soi.readObject();
				return t;
			} catch (final IOException e) {
				throw new ProtocolRuntimeException(e);
			} catch (final ClassNotFoundException e) {
				throw new ProtocolRuntimeException(e);
			} finally {
				next = null;
			}
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}

	private final StreamIterator<T> iterator;

	public InputServerStreamIterable(final SerializableObjectInput soi) {
		this.iterator = new StreamIterator<T>(soi);
	}

	@Override
	public void close() {
		// empty
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}

	@Override
	public void cancel() {
		close();
	}

	@Override
	public void write(final T item) {
		throw new UnsupportedOperationException();
	}

}
