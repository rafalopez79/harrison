package com.bzsoft.harrison.proto.stream.base;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;

public class BaseSerializableObjectOutput implements SerializableObjectOutput {

	private final ObjectOutputStream os;

	public BaseSerializableObjectOutput(final OutputStream ostream) throws IOException{
		os = new ObjectOutputStream(ostream);
	}

	@Override
	public void writeObject(final Object obj) throws IOException {
		os.writeObject(obj);
	}

	@Override
	public void write(final int b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(final byte[] b) throws IOException {
		os.write(b);
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		os.write(b, off, len);
	}

	@Override
	public void flush() throws IOException {
		os.flush();
	}

	@Override
	public void close() throws IOException {
		os.close();
	}

	@Override
	public void writeBoolean(final boolean v) throws IOException {
		os.writeBoolean(v);
	}

	@Override
	public void writeByte(final int v) throws IOException {
		os.writeByte(v);
	}

	@Override
	public void writeShort(final int v) throws IOException {
		os.writeShort(v);
	}

	@Override
	public void writeChar(final int v) throws IOException {
		os.writeChar(v);
	}

	@Override
	public void writeInt(final int v) throws IOException {
		os.writeInt(v);
	}

	@Override
	public void writeLong(final long v) throws IOException {
		os.writeLong(v);
	}

	@Override
	public void writeFloat(final float v) throws IOException {
		os.writeFloat(v);
	}

	@Override
	public void writeDouble(final double v) throws IOException {
		os.writeDouble(v);
	}

	@Override
	public void writeBytes(final String s) throws IOException {
		os.writeBytes(s);
	}

	@Override
	public void writeChars(final String s) throws IOException {
		os.writeChars(s);
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		os.writeUTF(s);
	}

	@Override
	public void writeResult(final Object o, final Throwable t) throws IOException {
		if (t != null){
			os.writeBoolean(false);
			os.writeObject(t);
		}else{
			os.writeBoolean(true);
			os.writeObject(o);
		}
	}

	@Override
	public void reset() throws IOException{
		os.reset();
	}

	@Override
	public void writeString(final String s) throws IOException {
		if (s == null){
			os.writeBoolean(false);
		}else{
			os.writeBoolean(true);
			os.writeUTF(s);
		}
	}

	@Override
	public void writeStreamBegin() throws IOException {
		os.writeByte(ProtocolConstants.STREAM_BEGINING);
	}

	@Override
	public void writeStreamEnd() throws IOException {
		os.writeByte(ProtocolConstants.STREAM_END);
	}



}
