package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;

public class OutputServerStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private final SerializableObjectOutput soi;

	public OutputServerStreamIterable(final SerializableObjectOutput soi) {
		this.soi = soi;
	}

	@Override
	public void close() throws IOException {
		//empty
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancel() throws IOException {
		close();
	}

	@Override
	public void write(final T item) throws IOException {
		soi.writeBoolean(true);
		soi.writeObject(item);
		soi.reset();
	}

}
