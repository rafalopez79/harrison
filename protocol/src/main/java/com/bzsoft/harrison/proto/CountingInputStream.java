package com.bzsoft.harrison.proto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class CountingInputStream extends FilterInputStream {

	private  long transferred;

	public CountingInputStream(final InputStream in) {
		super(in);
		transferred = 0;
	}

	@Override
	public int read() throws IOException {
		final int result =  super.read();
		if (result > -1){
			transferred ++;
		}
		return result;
	}

	@Override
	public int read(final byte[] b) throws IOException {
		final int result =  super.read();
		if (result > -1){
			transferred+=result;
		}
		return result;
	}

	@Override
	public int read(final byte[] b, final int off, final int len) throws IOException {
		final int result =  super.read(b, off, len);
		if (result > -1){
			transferred+=result;
		}
		return result;
	}

	public long getTransferred() {
		return transferred;
	}
}
