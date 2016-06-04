package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;

public class InputOutputServerStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private static final class StreamIterator<T> implements Iterator<T> {

		private final SerializableObjectInput soi;
		private final SerializableObjectOutput soo;
		private Boolean next;

		private StreamIterator(final SerializableObjectInput soi, final SerializableObjectOutput soo) {
			this.soi = soi;
			this.soo = soo;
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
					soo.writeBoolean(true);
					soo.writeStreamBegin();
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

		public boolean finish() {
			return next != null && !next.booleanValue();
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException("remove");
		}
	}

	private final SerializableObjectInput soi;
	private final SerializableObjectOutput soo;
	private final StreamIterator<T> iterator;
	private int count;

	public InputOutputServerStreamIterable(final SerializableObjectInput soi, final SerializableObjectOutput soo) {
		this.soi = soi;
		this.soo = soo;
		this.count = 0;
		this.iterator = new StreamIterator<T>(soi, soo);
	}

	@Override
	public void close() {
		//
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
		try{
			if (!iterator.finish()) {
				throw new IOException("Input not finished");
			}
			soo.writeBoolean(true);
			soo.writeObject(item);
			if (++count  == ProtocolConstants.RESET_COUNT){
				soo.reset();
				count = 0;
			}
		}catch(final IOException e){
			throw new ProtocolRuntimeException(e);
		}
	}

	@Override
	protected void finalize() throws Throwable {
		ProtocolUtil.close(soi, soo);
		super.finalize();
	}

}
