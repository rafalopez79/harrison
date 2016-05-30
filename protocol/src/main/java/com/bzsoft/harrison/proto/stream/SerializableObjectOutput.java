package com.bzsoft.harrison.proto.stream;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectOutput;

public interface SerializableObjectOutput extends ObjectOutput, Closeable{

	public void writeString(String s)  throws IOException;

	public void writeResult(Object o, Throwable t)  throws IOException;

	public void reset() throws IOException;

	public void writeStreamBegin() throws IOException;

	public void writeStreamEnd() throws IOException;

}
