package com.bzsoft.harrison.proto.stream.kryo;

import com.esotericsoftware.kryo.Kryo;

public class BaseKryoSerializable {

	protected final Kryo kryo;

	protected BaseKryoSerializable() {
		kryo = new AutoKryo();
		kryo.setDefaultSerializer(HarrisonSerializer.class);
	}

}
