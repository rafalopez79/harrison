package com.bzsoft.harrison.proto.stream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface SerializableObjectStreamFactory {

	public SerializableObjectInput createSerializableObjectInput(InputStream is) throws IOException;

	public SerializableObjectOutput createSerializableObjectOutput(OutputStream os) throws IOException;

	public byte getType();

}
