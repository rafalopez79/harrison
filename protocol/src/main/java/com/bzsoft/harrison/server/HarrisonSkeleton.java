package com.bzsoft.harrison.server;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bzsoft.harrison.proto.InputOutputServerStreamIterable;
import com.bzsoft.harrison.proto.OutputServerStreamIterable;
import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolException;
import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactoryCreator;
import com.bzsoft.harrison.proto.stream.impl.SerializableObjectStreamFactoryCreatorImpl;


public class HarrisonSkeleton extends AbstractSkeleton {

	private static final Logger	LOGGER	= LoggerFactory.getLogger(HarrisonSkeleton.class);

	private final Object				service;
	private final SerializableObjectStreamFactoryCreator sosFactoryCreator;


	public HarrisonSkeleton(final Object service, final Class<?> apiClass) {
		super(apiClass);
		this.service = service;
		if (!apiClass.isAssignableFrom(service.getClass())) {
			throw new IllegalArgumentException("Service " + service + " must be an instance of " + apiClass.getName());
		}
		this.sosFactoryCreator = new SerializableObjectStreamFactoryCreatorImpl();
	}


	public void invoke(final InputStream is, final OutputStream os) throws Exception {
		final int header = ProtocolUtil.readInt(is);
		if (header != ProtocolConstants.MAGIC) {
			throw new ProtocolException("Invalid format");
		}
		final int version = ProtocolUtil.readInt(is);
		if (version != ProtocolConstants.VERSION){
			throw new ProtocolException("Invalid version");
		}
		final byte type = ProtocolUtil.readByte(is);
		final byte call = ProtocolUtil.readByte(is);
		final SerializableObjectStreamFactory sosFactory = sosFactoryCreator.create(type);
		if (call == ProtocolConstants.STANDARD_CALL){
			SerializableObjectInput soi = null;
			SerializableObjectOutput soo = null;
			try{
				soi = sosFactory.createSerializableObjectInput(is);
				soo = sosFactory.createSerializableObjectOutput(os);
				invoke(service, soi,soo, false);
			}finally{
				ProtocolUtil.close(soi, soo);
			}
		}else if (call == ProtocolConstants.STREAM_CALL){
			SerializableObjectInput soi = null;
			SerializableObjectOutput soo = null;
			try{
				soi = sosFactory.createSerializableObjectInput(is);
				soo = sosFactory.createSerializableObjectOutput(os);
				invoke(service, soi,soo, true);
			}finally{
				ProtocolUtil.close(soi, soo);
			}
		}else{
			throw new ProtocolException(String.format("invalid call '%c' format", call));
		}
	}


	private  void invoke(final Object srv, final SerializableObjectInput in, final SerializableObjectOutput out, final boolean stream) throws Exception {
		//readMethodCall
		final String methodName = in.readUTF();
		int argLength = in.readInt();
		final Method method = getMethod(methodName);
		if (method == null) {
			out.writeResult(null, new NoSuchMethodException(escapeMessage("The service has no method named: " + methodName)));
			out.close();
			return;
		}
		final Class<?>[] args = method.getParameterTypes();
		final Object[] values;
		if (stream){
			argLength++;
		}
		if (argLength != args.length && argLength >= 0) {
			out.writeResult(null, new NoSuchMethodException(escapeMessage("method " + method + " argument length mismatch, received length=" + argLength)));
			out.close();
			return;
		}
		if(stream){
			values = new Object[argLength];
			for (int i = 0; i < argLength-1; i++) {
				values[i] = in.readObject();
			}
			final boolean hasInputStream = in.readBoolean();
			if (hasInputStream){
				in.readStreamBegin();
				values[args.length -1] = new InputOutputServerStreamIterable<Serializable>(in, out);
			}else{
				values[args.length -1] = new OutputServerStreamIterable<Serializable>(out);
				out.writeBoolean(true);
				out.writeStreamBegin();
			}
		}else{
			values = new Object[args.length];
			for (int i = 0; i < args.length; i++) {
				values[i] = in.readObject();
			}
		}
		Throwable throwable = null;
		Object result = null;
		try {
			result = method.invoke(srv, values);
		} catch (final Throwable e) {
			throwable = e;
			if (throwable instanceof InvocationTargetException) {
				throwable = ((InvocationTargetException) e).getTargetException();
			}
			LOGGER.debug("{} {}", this, throwable.toString(), throwable);
		}
		if (stream){
			out.writeBoolean(false);
			out.writeStreamEnd();
			out.writeObject(throwable);
		}else{
			out.writeResult(result, throwable);
			out.close();
		}
	}

	private static String escapeMessage(final String msg) {
		if (msg == null) {
			return null;
		}
		final StringBuilder sb = new StringBuilder();
		final int length = msg.length();
		for (int i = 0; i < length; i++) {
			final char ch = msg.charAt(i);
			switch (ch) {
			case '<':
				sb.append("&lt;");
				break;
			case '>':
				sb.append("&gt;");
				break;
			case 0x0:
				sb.append("&#00;");
				break;
			case '&':
				sb.append("&amp;");
				break;
			default:
				sb.append(ch);
				break;
			}
		}
		return sb.toString();
	}
}
