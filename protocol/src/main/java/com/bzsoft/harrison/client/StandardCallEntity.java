package com.bzsoft.harrison.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.entity.AbstractHttpEntity;

import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;

/**
 * The Class StandardCallEntity.
 */
public class StandardCallEntity extends AbstractHttpEntity {

	private final String										methodName;
	private final Object[]									args;
	private final SerializableObjectStreamFactory	sosFactory;

	/**
	 * Creates new instance of this class.
	 *
	 * @param methodName
	 *           the method name
	 * @param args
	 *           the args
	 * @param sosFactory
	 *           the sos factory
	 * @throws IOException
	 *            in case of an I/O error
	 */
	public StandardCallEntity(final String methodName, final Object[] args, final SerializableObjectStreamFactory sosFactory) throws IOException {
		this.methodName = methodName;
		this.args = args;
		this.sosFactory = sosFactory;
		setChunked(true);
		setContentType(ProtocolConstants.CONTENT_TYPE);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InputStream getContent() throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long getContentLength() {
		return -1;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isRepeatable() {
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStreaming() {
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeTo(final OutputStream outstream) throws IOException {
		if (outstream == null) {
			throw new IllegalArgumentException("Output stream may not be null");
		}
		ProtocolUtil.writeInt(ProtocolConstants.MAGIC, outstream);
		ProtocolUtil.writeInt(ProtocolConstants.VERSION, outstream);
		ProtocolUtil.writeByte(sosFactory.getType(), outstream);
		ProtocolUtil.writeByte(ProtocolConstants.STANDARD_CALL, outstream);
		final SerializableObjectOutput os = sosFactory.createSerializableObjectOutput(outstream);
		// call
		os.writeUTF(methodName);
		os.writeInt(args.length);
		for (final Object o : args) {
			os.writeObject(o);
		}
		os.flush();
		os.close();
	}
}
