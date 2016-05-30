package com.bzsoft.harrison.proto.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInput;

public interface SerializableObjectInput extends ObjectInput, Closeable {

	public String readString() throws IOException;

	public Object readResult() throws Throwable;

	public void readStreamBegin() throws IOException;

	public void readStreamEnd() throws IOException;

}
