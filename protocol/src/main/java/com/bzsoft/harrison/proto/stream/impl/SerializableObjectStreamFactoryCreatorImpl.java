package com.bzsoft.harrison.proto.stream.impl;

import com.bzsoft.harrison.proto.ProtocolRuntimeException;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactoryCreator;
import com.bzsoft.harrison.proto.stream.SerializerType;
import com.bzsoft.harrison.proto.stream.base.BaseSerializableObjectStreamFactoryImpl;
import com.bzsoft.harrison.proto.stream.kryo.KryoSerializableObjectStreamFactoryImpl;

public class SerializableObjectStreamFactoryCreatorImpl implements SerializableObjectStreamFactoryCreator {

	@Override
	public SerializableObjectStreamFactory create(final SerializerType type) {
		switch (type) {
		case JAVA:
			return new BaseSerializableObjectStreamFactoryImpl();
		case KRYO:
			return new KryoSerializableObjectStreamFactoryImpl();
		default:
			throw new ProtocolRuntimeException(String.format("Unknown serialization %d", type));
		}
	}

}
