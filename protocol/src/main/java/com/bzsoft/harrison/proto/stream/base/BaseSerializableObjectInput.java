package com.bzsoft.harrison.proto.stream.base;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;

public class BaseSerializableObjectInput implements SerializableObjectInput {

    private final ObjectInputStream is;

    public BaseSerializableObjectInput(final InputStream is) throws IOException {
        this.is = new ObjectInputStream(is);
    }

    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        return is.readObject();
    }

    @Override
    public void close() throws IOException {
        is.close();
    }

    @Override
    public boolean readBoolean() throws IOException {
        return is.readBoolean();
    }

    @Override
    public int readInt() throws IOException {
        return is.readInt();
    }

    @Override
    public String readUTF() throws IOException {
        return is.readUTF();
    }

    @Override
    public String readString() throws IOException {
        final boolean notNull = is.readBoolean();
        if (notNull) {
            return is.readUTF();
        }
        return null;
    }

    @Override
    public Object readResult() throws Throwable {
        final boolean ok = is.readBoolean();
        if (ok) {
            return readObject();
        }
        final Throwable t = (Throwable) readObject();
        throw t;
    }

    @Override
    public void readStreamBegin() throws IOException {
        final byte b = is.readByte();
        if (b != ProtocolConstants.STREAM_BEGINING) {
            throw new IOException("Stream begining not valid");
        }
    }

    @Override
    public void readStreamEnd() throws IOException {
        final byte b = is.readByte();
        if (b != ProtocolConstants.STREAM_END) {
            throw new IOException("Stream ending not valid");
        }
    }

}
