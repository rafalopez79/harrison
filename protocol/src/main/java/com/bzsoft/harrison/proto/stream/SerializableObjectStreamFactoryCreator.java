package com.bzsoft.harrison.proto.stream;

import com.bzsoft.harrison.proto.ProtocolException;

public interface SerializableObjectStreamFactoryCreator {

	public SerializableObjectStreamFactory create(byte type) throws ProtocolException;

}
