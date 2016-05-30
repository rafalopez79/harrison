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
	public int read() throws IOException {
		return is.read();
	}

	@Override
	public int read(final byte[] b) throws IOException {
		return is.read(b);
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		return is.read(b, off, len);
	}

	@Override
	public long skip(final long n) throws IOException {
		return is.skip(n);
	}

	@Override
	public int available() throws IOException {
		return is.available();
	}

	@Override
	public void close() throws IOException {
		is.close();
	}

	@Override
	public void readFully(final byte[] b) throws IOException {
		is.readFully(b);
	}

	@Override
	public void readFully(final byte[] b, final int off, final int len) throws IOException {
		is.readFully(b, off, len);
	}

	@Override
	public int skipBytes(final int n) throws IOException {
		return is.skipBytes(n);
	}

	@Override
	public boolean readBoolean() throws IOException {
		return is.readBoolean();
	}

	@Override
	public byte readByte() throws IOException {
		return is.readByte();
	}

	@Override
	public int readUnsignedByte() throws IOException {
		return is.readUnsignedByte();
	}

	@Override
	public short readShort() throws IOException {
		return is.readShort();
	}

	@Override
	public int readUnsignedShort() throws IOException {
		return is.readUnsignedShort();
	}

	@Override
	public char readChar() throws IOException {
		return is.readChar();
	}

	@Override
	public int readInt() throws IOException {
		return is.readInt();
	}

	@Override
	public long readLong() throws IOException {
		return is.readLong();
	}

	@Override
	public float readFloat() throws IOException {
		return is.readFloat();
	}

	@Override
	public double readDouble() throws IOException {
		return is.readDouble();
	}

	@Override
	@Deprecated
	public String readLine() throws IOException {
		return is.readLine();
	}

	@Override
	public String readUTF() throws IOException {
		return is.readUTF();
	}

	@Override
	public String readString() throws IOException {
		final boolean notNull = is.readBoolean();
		if (notNull){
			return is.readUTF();
		}
		return null;
	}

	@Override
	public Object readResult() throws Throwable {
		final boolean ok = is.readBoolean();
		if (ok){
			return readObject();
		}
		final Throwable t = (Throwable) readObject();
		throw t;
	}

	@Override
	public void readStreamBegin() throws IOException {
		final byte b = is.readByte();
		if (b != ProtocolConstants.STREAM_BEGINING){
			throw new IOException("Stream begining not valid");
		}
	}

	@Override
	public void readStreamEnd() throws IOException {
		final byte b = is.readByte();
		if (b != ProtocolConstants.STREAM_END){
			throw new IOException("Stream ending not valid");
		}
	}

}
