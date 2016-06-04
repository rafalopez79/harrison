package com.bzsoft.harrison.proto;

import java.io.Closeable;
import java.io.Serializable;
import java.util.Iterator;

public interface StreamIterable<T extends Serializable> extends Iterable<T>, Closeable{

	@Override
	public void close();

	@Override
	public Iterator<T> iterator();

	public void cancel();

	public void write(T item);

}
