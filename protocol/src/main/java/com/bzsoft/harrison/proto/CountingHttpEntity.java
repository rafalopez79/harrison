package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;

public class CountingHttpEntity extends HttpEntityWrapper {

	private CountingOutputStream cos;
	private CountingInputStream cis;

	public CountingHttpEntity(final HttpEntity entity) {
		super(entity);

	}

	@Override
	public void writeTo(final OutputStream out) throws IOException {
		if (out instanceof CountingOutputStream){
			cos = (CountingOutputStream)out;
		}else{
			cos = new CountingOutputStream(out);
		}
		this.wrappedEntity.writeTo(cos);
	}

	@Override
	public InputStream getContent() throws IOException {
		final InputStream is =  super.getContent();
		if (is instanceof CountingInputStream){
			cis = (CountingInputStream)is;
		}else{
			cis = new CountingInputStream(is);
		}
		return cis;
	}

	public long getTransferred(){
		if (cos != null){
			return cos.getTransferred();
		}else if (cis != null){
			return cis.getTransferred();
		}
		return 0;
	}
}
