package com.bzsoft.harrison.proto.stream.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.serializers.FieldSerializer;

public class HarrisonSerializer<T> extends FieldSerializer<T> {

	public HarrisonSerializer(final Kryo kryo, final Class<T> type) {
		super(kryo, type);
	}



}
