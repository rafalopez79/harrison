package com.bzsoft.harrison.proto;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;


public final class ProtocolUtil {

	private ProtocolUtil() {
		// empty
	}

	public static void close(final Closeable c){
		if (c != null){
			try{
				c.close();
			}catch(final IOException e){
				//empty
			}
		}
	}

	public static void close(final Closeable ...closeables){
		for(final Closeable c : closeables){
			close(c);
		}
	}

	public static void writeByte(final int v, final OutputStream out) throws IOException{
		out.write(v);
	}

	public static void writeInt(final int v, final OutputStream out) throws IOException{
		out.write(v >>> 24 & 0xFF);
		out.write(v >>> 16 & 0xFF);
		out.write(v >>>  8 & 0xFF);
		out.write(v >>>  0 & 0xFF);
	}

	public static byte readByte(final InputStream in) throws IOException{
		final int b = in.read();
		if (b < 0){
			throw new EOFException();
		}
		return (byte)b;
	}

	public static int readInt(final InputStream in) throws IOException{
		final int ch1 = in.read();
		final int ch2 = in.read();
		final int ch3 = in.read();
		final int ch4 = in.read();
		if ((ch1 | ch2 | ch3 | ch4) < 0) {
			throw new EOFException();
		}
		return (ch1 << 24) + (ch2 << 16) + (ch3 << 8) + (ch4 << 0);
	}

	public static void main(final String[] args) {
		final Class<?> type = ProtocolUtil.class;
		final Method[] ms = type.getMethods();
		for(final Method m : ms){
			System.out.println(m.toGenericString());
		}
	}
}
