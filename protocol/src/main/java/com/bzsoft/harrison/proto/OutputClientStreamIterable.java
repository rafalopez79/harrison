package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bzsoft.harrison.proto.stream.SerializableObjectInput;

public class OutputClientStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutputClientStreamIterable.class);

	private static final class StreamIterator<T> implements Iterator<T>{

		private final SerializableObjectInput soi;
		private Boolean next;

		private StreamIterator(final SerializableObjectInput soi) {
			this.soi = soi;
			this.next = null;
		}

		@Override
		public boolean hasNext() {
			try{
				if (next != null){
					return next.booleanValue();
				}
				final boolean streamNext = soi.readBoolean();
				next = Boolean.valueOf(streamNext);
				if (!streamNext){
					soi.readStreamEnd();
					final Throwable t = (Throwable) soi.readObject();
					if (t != null){
						throw t;
					}
				}
				return streamNext;
			}catch(final IOException e){
				throw new ProtocolRuntimeException(e);
			}catch (final Throwable e) {
				throw new ProtocolRuntimeException(e);
			}
		}

		@Override
		public T next(){
			try{
				if (next == null){
					final boolean hasNext = hasNext();
					if (!hasNext){
						throw new NoSuchElementException();
					}
				}
				@SuppressWarnings("unchecked")
				final T t = (T) soi.readObject();
				return t;
			}catch(final IOException e){
				throw new ProtocolRuntimeException(e);
			}catch(final ClassNotFoundException e){
				throw new ProtocolRuntimeException(e);
			}finally{
				next = null;
			}
		}

		@Override
		public void remove(){
			throw new UnsupportedOperationException("remove");
		}
	}

	private final SerializableObjectInput soi;
	private final CountingHttpEntity entity;
	private final Iterator<T> iterator;

	public OutputClientStreamIterable(final SerializableObjectInput soi, final CountingHttpEntity entity) {
		this.soi = soi;
		this.entity = entity;
		this.iterator = new StreamIterator<T>(soi);
	}

	@Override
	public void close() throws IOException {
		soi.close();
		LOGGER.info("Downloaded {} bytes", entity.getTransferred());
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}

	@Override
	public void cancel() throws IOException {
		close();
	}

	@Override
	public void write(final T item) throws IOException {
		throw new UnsupportedOperationException();
	}

	@Override
	protected void finalize() throws Throwable {
		ProtocolUtil.close(soi);
		super.finalize();
	}

}
