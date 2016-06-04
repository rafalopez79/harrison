package com.bzsoft.harrison.client.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.TimeUnit;

import org.apache.http.ExceptionLogger;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.message.BasicHttpEntityEnclosingRequest;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestHandler;

import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.server.HarrisonSkeleton;

public class TestClient {

	private static final void startServer(final Object service, final Class<?> apiClass) throws Exception{

		final HarrisonSkeleton skel = new HarrisonSkeleton(service, apiClass);
		final HttpRequestHandler requestHandler = new HttpRequestHandler() {



			@Override
			public void handle(final HttpRequest request, final HttpResponse response, final HttpContext context) throws HttpException, IOException {
				final BasicHttpEntityEnclosingRequest ec = request instanceof BasicHttpEntityEnclosingRequest ? (BasicHttpEntityEnclosingRequest) request: null;
				final BasicHttpRequest rq = (BasicHttpRequest)request;
				final BasicHttpResponse re = (BasicHttpResponse)response;
				final InputStream is = ec == null? new ByteArrayInputStream(new byte[0]) : ec.getEntity().getContent();
				final OutputStream os = null;
				//final OutputStream os = re.getEntity().
				try{
					skel.invoke(is, os);
				} catch (final Exception e) {
					e.printStackTrace();
				}finally{
					ProtocolUtil.close(is, os);
				}
			}
		};
		final HttpProcessor httpProcessor = HttpProcessorBuilder.create().build();
		final SocketConfig socketConfig = SocketConfig.custom()
				.setSoTimeout(15000)
				.setTcpNoDelay(true)
				.build();
		final HttpServer server = ServerBootstrap.bootstrap()
				.setListenerPort(8080)
				.setHttpProcessor(httpProcessor)
				.setSocketConfig(socketConfig)
				.setExceptionLogger(ExceptionLogger.STD_ERR)
				.registerHandler("/services/test", requestHandler)
				.create();
		server.start();
		server.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				server.shutdown(5, TimeUnit.SECONDS);
			}
		});
	}

	public static void main(final String[] args) throws Exception{
		startServer(null, null);
	}
}
