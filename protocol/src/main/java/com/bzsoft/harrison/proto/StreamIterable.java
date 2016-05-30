package com.bzsoft.harrison.proto;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

public interface StreamIterable<T extends Serializable> extends Iterable<T>, Closeable{

	@Override
	public void close() throws IOException;

	@Override
	public Iterator<T> iterator();

	public void cancel() throws IOException;

	public void write(T item) throws IOException;

}
