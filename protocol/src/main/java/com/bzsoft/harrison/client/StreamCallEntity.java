package com.bzsoft.harrison.client;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;

import org.apache.http.entity.AbstractHttpEntity;

import com.bzsoft.harrison.proto.IterableWrapper;
import com.bzsoft.harrison.proto.IteratorException;
import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;

/**
 * The Class StreamCallEntity.
 *
 * @param <T>
 *            the generic type
 */
public class StreamCallEntity<T extends Serializable> extends AbstractHttpEntity {

	private final String methodName;
	private final Object[] args;
	private final Iterable<T> iterable;
	private final byte callType;
	private final SerializableObjectStreamFactory sosFactory;

	/**
	 * Creates new instance of this class.
	 *
	 * @param methodName
	 *            the method name
	 * @param args
	 *            the args
	 * @param iterable
	 *            the iterable
	 * @param sosFactory
	 *            the sos factory
	 * @throws IOException
	 *             in case of an I/O error
	 */
	public StreamCallEntity(final String methodName, final Object[] args,
			final Iterable<T> iterable, final byte callType,
			final SerializableObjectStreamFactory sosFactory) throws IOException {
		this.methodName = methodName;
		this.args = args;
		this.iterable = iterable == null ? null : IterableWrapper.of(iterable);
		this.callType = callType;
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
		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean isStreaming() {
		return true;
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
		ProtocolUtil.writeByte(callType, outstream);
		final SerializableObjectOutput os = sosFactory.createSerializableObjectOutput(outstream);
		// call
		os.writeUTF(methodName);
		os.writeInt(args.length);
		for (final Object o : args) {
			os.writeObject(o);
		}
		if (iterable != null
				&& (callType == ProtocolConstants.UPLOAD_CALL || callType == ProtocolConstants.UPLOADDOWNLOAD_CALL)) {
			os.writeBoolean(true); // has upload stream
			os.writeStreamBegin();
			try {
				int count = 0;
				for (final T item : iterable) {
					os.writeBoolean(true);
					os.writeObject(item);
					if (++count  == ProtocolConstants.RESET_COUNT){
						os.reset();
						count = 0;
					}
				}
				os.writeBoolean(false);
				os.writeStreamEnd();
				os.writeObject(null);
			} catch (final IteratorException e) {
				// exception while iterating
				os.writeBoolean(false);
				os.writeStreamEnd();
				os.writeObject(e.getCause());
			}

		} else {
			os.writeBoolean(false); // has not streaming
		}
		os.flush();
		os.close();
	}
}
