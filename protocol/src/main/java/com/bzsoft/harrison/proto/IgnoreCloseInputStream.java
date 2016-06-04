package com.bzsoft.harrison.proto;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

public class IgnoreCloseInputStream extends FilterInputStream {

	public IgnoreCloseInputStream(final InputStream in) {
		super(in);
	}

	@Override
	public void close() throws IOException {
		//empty
	}

}
