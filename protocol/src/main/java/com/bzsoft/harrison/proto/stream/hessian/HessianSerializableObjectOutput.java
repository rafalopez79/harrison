package com.bzsoft.harrison.proto.stream.hessian;

import java.io.IOException;
import java.io.OutputStream;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.caucho.hessian.io.Hessian2Output;

public class HessianSerializableObjectOutput implements SerializableObjectOutput {

	private final Hessian2Output os;

	public HessianSerializableObjectOutput(final OutputStream ostream) throws IOException {
		os = new Hessian2Output(ostream);
	}

	@Override
	public void writeObject(final Object obj) throws IOException {
		os.writeObject(obj);
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
	public void writeInt(final int v) throws IOException {
		os.writeInt(v);
	}

	@Override
	public void writeUTF(final String s) throws IOException {
		os.writeString(s);
	}

	@Override
	public void writeResult(final Object o, final Throwable t) throws IOException {
		if (t != null) {
			os.writeBoolean(false);
			os.writeObject(t);
		} else {
			os.writeBoolean(true);
			os.writeObject(o);
		}
	}

	@Override
	public void reset() throws IOException {
		os.reset();
	}

	@Override
	public void writeString(final String s) throws IOException {
		if (s == null) {
			os.writeBoolean(false);
		} else {
			os.writeBoolean(true);
			os.writeString(s);
		}
	}

	@Override
	public void writeStreamBegin() throws IOException {
		os.writeInt(ProtocolConstants.STREAM_BEGINING);
	}

	@Override
	public void writeStreamEnd() throws IOException {
		os.writeInt(ProtocolConstants.STREAM_END);
	}

}
