package com.bzsoft.harrison.proto.stream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolRuntimeException;

public enum SerializerType {
	JAVA(ProtocolConstants.JAVA_SERIALIZATION), KRYO(ProtocolConstants.KRYO_SERIALIZATION);

	private final byte type;

	private SerializerType(final byte type) {
		this.type = type;
	}

	public static final SerializerType of(final byte type){
		switch (type) {
		case ProtocolConstants.JAVA_SERIALIZATION:
			return JAVA;
		case ProtocolConstants.KRYO_SERIALIZATION:
			return KRYO;
		default:
			throw new ProtocolRuntimeException(String.format("Unknown serialization %d", type));
		}
	}

	public byte getType() {
		return type;
	}

}
