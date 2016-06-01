package com.bzsoft.harrison.proto.stream.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;

public class KryoSerializableObjectStreamFactoryImpl implements SerializableObjectStreamFactory {

    @Override
    public SerializableObjectInput createSerializableObjectInput(final InputStream is) throws IOException {
        return new KryoSerializableObjectInput(is);
    }

    @Override
    public SerializableObjectOutput createSerializableObjectOutput(final OutputStream os) throws IOException {
        return new KryoSerializableObjectOutput(os);
    }

    @Override
    public byte getType() {
        return ProtocolConstants.KRYO_SERIALIZATION;
    }

}
