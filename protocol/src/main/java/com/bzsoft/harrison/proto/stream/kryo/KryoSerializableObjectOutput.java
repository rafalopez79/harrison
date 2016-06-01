package com.bzsoft.harrison.proto.stream.kryo;

import java.io.IOException;
import java.io.OutputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;

/**
 * The Class KryoSerializableObjectOutput.
 */
public class KryoSerializableObjectOutput implements SerializableObjectOutput {

    private final Kryo kryo;
    private final Output os;

    /**
     * Instantiates a new kryo serializable object output.
     * 
     * @param ostream
     *            the ostream
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public KryoSerializableObjectOutput(final OutputStream ostream) throws IOException {
        kryo = new Kryo();
        os = new Output(ostream);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeObject(final Object obj) throws IOException {
        kryo.writeClassAndObject(os, obj);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void flush() throws IOException {
        os.flush();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void close() throws IOException {
        os.close();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeBoolean(final boolean v) throws IOException {
        os.writeBoolean(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeInt(final int v) throws IOException {
        os.writeInt(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeUTF(final String s) throws IOException {
        os.writeString(s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeResult(final Object o, final Throwable t) throws IOException {
        if (t != null) {
            os.writeBoolean(false);
            kryo.writeClassAndObject(os, t);
        } else {
            os.writeBoolean(true);
            kryo.writeClassAndObject(os, o);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() throws IOException {
        kryo.reset();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeString(final String s) throws IOException {
        kryo.writeClassAndObject(os, s);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStreamBegin() throws IOException {
        os.writeByte(ProtocolConstants.STREAM_BEGINING);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void writeStreamEnd() throws IOException {
        os.writeByte(ProtocolConstants.STREAM_END);
    }

}
