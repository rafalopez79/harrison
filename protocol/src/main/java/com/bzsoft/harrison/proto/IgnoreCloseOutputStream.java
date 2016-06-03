package com.bzsoft.harrison.proto;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class IgnoreCloseOutputStream extends FilterOutputStream {

	public IgnoreCloseOutputStream(final OutputStream out) {
		super(out);
	}

	@Override
	public void close() throws IOException {
		//empty
	}

}
