package com.bzsoft.harrison.server.spring;

import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteExporter;
import org.springframework.util.Assert;

import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.server.HarrisonSkeleton;

public class HarrisonExporter extends RemoteExporter implements InitializingBean {

	private HarrisonSkeleton	skeleton;

	@Override
	public void afterPropertiesSet() {
		prepare();
	}

	public void prepare() {
		checkService();
		checkServiceInterface();
		this.skeleton = new HarrisonSkeleton(getProxyForService(), getServiceInterface());
	}

	public void invoke(final InputStream inputStream, final OutputStream outputStream) throws Throwable {
		Assert.notNull(this.skeleton, "HarrisonSkeleton has not been initialized");
		doInvoke(this.skeleton, inputStream, outputStream);
	}

	protected void doInvoke(final HarrisonSkeleton s, final InputStream inputStream, final OutputStream outputStream) throws Throwable {
		final ClassLoader originalClassLoader = overrideThreadContextClassLoader();
		try {
			s.invoke(inputStream, outputStream);
		} finally {
			ProtocolUtil.close(inputStream, outputStream);
			resetThreadContextClassLoader(originalClassLoader);
		}
	}
}