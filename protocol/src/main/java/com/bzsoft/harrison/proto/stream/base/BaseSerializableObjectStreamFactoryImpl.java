package com.bzsoft.harrison.proto.stream.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;

public class BaseSerializableObjectStreamFactoryImpl implements SerializableObjectStreamFactory {

	@Override
	public SerializableObjectInput createSerializableObjectInput(final InputStream is)  throws IOException{
		return new BaseSerializableObjectInput(is);
	}

	@Override
	public SerializableObjectOutput createSerializableObjectOutput(final OutputStream os)  throws IOException{
		return new BaseSerializableObjectOutput(os);
	}

	@Override
	public byte getType() {
		return ProtocolConstants.JAVA_SERIALIZATION;
	}

}
