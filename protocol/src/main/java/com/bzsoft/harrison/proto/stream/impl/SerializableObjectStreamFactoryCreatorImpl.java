package com.bzsoft.harrison.proto.stream.impl;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolException;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactoryCreator;
import com.bzsoft.harrison.proto.stream.base.BaseSerializableObjectStreamFactoryImpl;

public class SerializableObjectStreamFactoryCreatorImpl implements SerializableObjectStreamFactoryCreator{

	@Override
	public SerializableObjectStreamFactory create(final byte type) throws ProtocolException {
		switch (type) {
		case ProtocolConstants.JAVA_SERIALIZATION:
			return new BaseSerializableObjectStreamFactoryImpl();
		default:
			throw new ProtocolException(String.format("Unknown serialization %d", type));
		}
	}

}
