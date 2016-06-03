package com.bzsoft.harrison.proto;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;



public  class CountingOutputStream extends FilterOutputStream {

	private long transferred;

	public CountingOutputStream(final OutputStream out) {
		super(out);
		transferred = 0;
	}

	@Override
	public void write(final byte[] b, final int off, final int len) throws IOException {
		out.write(b, off, len);
		transferred += len;
	}

	@Override
	public void write(final int b) throws IOException {
		out.write(b);
		transferred++;
	}

	public long getTransferred(){
		return transferred;
	}
}