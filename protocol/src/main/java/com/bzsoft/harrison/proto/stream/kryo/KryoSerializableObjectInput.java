package com.bzsoft.harrison.proto.stream.kryo;

import java.io.IOException;
import java.io.InputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;

/**
 * The Class KryoSerializableObjectInput.
 */
public class KryoSerializableObjectInput implements SerializableObjectInput {

    private final Kryo kryo;
    private final Input is;

    /**
     * Instantiates a new kryo serializable object input.
     * 
     * @param is
     *            the is
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public KryoSerializableObjectInput(final InputStream is) throws IOException {
        kryo = new Kryo();
        this.is = new Input(is);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readObject() throws ClassNotFoundException, IOException {
        return kryo.readClassAndObject(is);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        is.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean readBoolean() throws IOException {
        return is.readBoolean();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int readInt() throws IOException {
        return is.readInt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readUTF() throws IOException {
        return is.readString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String readString() throws IOException {
        return kryo.readObject(is, String.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object readResult() throws Throwable {
        final boolean ok = is.readBoolean();
        if (ok) {
            return readObject();
        }
        final Throwable t = (Throwable) readObject();
        throw t;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readStreamBegin() throws IOException {
        final byte b = is.readByte();
        if (b != ProtocolConstants.STREAM_BEGINING) {
            throw new IOException("Stream begining not valid");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void readStreamEnd() throws IOException {
        final byte b = is.readByte();
        if (b != ProtocolConstants.STREAM_END) {
            throw new IOException("Stream ending not valid");
        }
    }

}
