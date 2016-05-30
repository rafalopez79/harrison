
package com.bzsoft.harrison.client;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bzsoft.harrison.proto.OutputClientStreamEmptyIterable;
import com.bzsoft.harrison.proto.OutputClientStreamIterable;
import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolException;
import com.bzsoft.harrison.proto.ProtocolRuntimeException;
import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.base.BaseSerializableObjectStreamFactoryImpl;

/**
 * The Class ClientProxy.
 *
 *
 * GzipCompressingEntity
 */
public class ClientProxy implements InvocationHandler, Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientProxy.class);

	private final Map<Method, String> mangleMap = new WeakHashMap<Method, String>();
	private final CloseableHttpClient client;
	private final Class<?> type;
	private final URL url;
	private final SerializableObjectStreamFactory sosFactory;

	public ClientProxy(final URL url, final Class<?> type, final CloseableHttpClient client) {
		this.url = url;
		this.type = type;
		this.client = client;
		sosFactory = new BaseSerializableObjectStreamFactoryImpl();
	}

	/**
	 * Returns the proxy's URL.
	 *
	 * @return the url
	 */
	public URL getURL() {
		return url;
	}

	public Class<?> getType() {
		return type;
	}

	/**
	 * Handles the object invocation.
	 *
	 * @param proxy
	 *            the proxy object to invoke
	 * @param method
	 *            the method to call
	 * @param args
	 *            the arguments to the proxy object
	 * @return the object
	 * @throws Throwable
	 *             the throwable
	 */
	@Override
	public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
		String mangleName;
		synchronized (mangleMap) {
			mangleName = mangleMap.get(method);
		}
		if (mangleName == null) {
			final String methodName = method.getName();
			final Class<?>[] params = method.getParameterTypes();
			if (methodName.equals("equals") && params.length == 1 && params[0].equals(Object.class)) {
				final Object value = args[0];
				if (value == null || !Proxy.isProxyClass(value.getClass())) {
					return Boolean.FALSE;
				}
				final Object proxyHandler = Proxy.getInvocationHandler(value);
				if (!(proxyHandler instanceof ClientProxy)) {
					return Boolean.FALSE;
				}
				final ClientProxy handler = (ClientProxy) proxyHandler;
				return Boolean.valueOf(url.equals(handler.getURL()));
			} else if (methodName.equals("hashCode") && params.length == 0) {
				return  Integer.valueOf(url.hashCode());
			} else if (methodName.equals("getType") && params.length == 0) {
				return proxy.getClass().getInterfaces()[0].getName();
			} else if (methodName.equals("getURL") && params.length == 0) {
				return url.toString();
			} else if (methodName.equals("toString") && params.length == 0) {
				return "HarrisonProxy[" + url + "]";
			}
			mangleName = method.toGenericString();
			synchronized (mangleMap) {
				mangleMap.put(method, mangleName);
			}
		}
		final boolean streamCall = isStreamCall(method);
		try {
			if (streamCall){
				LOGGER.info("Stream call {} {} {}",url, mangleName, args);
				return streamCall(client, url, sosFactory, mangleName, args);
			}
			LOGGER.info("Standard call {} {} {}",url, mangleName, args);
			return standardCall(client, url, sosFactory, mangleName, args);
		} catch (final ProtocolException e) {
			throw new ProtocolRuntimeException(e);
		} catch (final Throwable t) {
			throw t;
		} finally {
			//empty
		}
	}

	private static final Object standardCall(final CloseableHttpClient client, final URL url,
			final SerializableObjectStreamFactory sosFactory, final String methodName, final Object[] args) throws Throwable {
		CloseableHttpResponse response = null;
		try {
			final HttpPost post = new HttpPost(url.toString());
			post.setEntity(new StandardCallEntity(methodName, args, sosFactory));
			response = client.execute(post);
			final StatusLine sl = response.getStatusLine();
			final int statusCode = sl.getStatusCode();
			final String statusMessage = sl.getReasonPhrase();
			LOGGER.info("Call status {} {}", statusCode, statusMessage);
			final HttpEntity entity = response.getEntity();
			if (entity != null && entity.isStreaming()) {
				try{
					final InputStream is = entity.getContent();
					final SerializableObjectInput soi = sosFactory.createSerializableObjectInput(is);
					final Object result = soi.readResult();
					return result;
				}catch(final Exception e){
					EntityUtils.consume(entity);
				}
			}
			throw new ProtocolException(statusMessage);
		} catch(final Exception e){
			if (response != null) {
				response.close();
			}
			throw e;
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final StreamIterable streamCall(final CloseableHttpClient client, final URL url,
			final SerializableObjectStreamFactory sosFactory, final String methodName, final Object[] args) throws Throwable{
		CloseableHttpResponse response = null;
		try {
			final HttpPost post = new HttpPost(url.toString());
			final int len = args.length;
			final Object[] mangledArgs = new Object[len-1];
			System.arraycopy(args, 0, mangledArgs, 0, len-2);
			final Iterable<?> iterable = (Iterable<?>) args[len -1];
			post.setEntity(new StreamCallEntity(methodName, mangledArgs, iterable, sosFactory));
			response = client.execute(post);
			final StatusLine sl = response.getStatusLine();
			final int statusCode = sl.getStatusCode();
			final String statusMessage = sl.getReasonPhrase();
			LOGGER.info("Streamed Call status {} {}", statusCode, statusMessage);
			final HttpEntity entity = response.getEntity();
			if (entity != null && entity.isStreaming()) {
				try{
					final InputStream is = entity.getContent();
					final SerializableObjectInput soi = sosFactory.createSerializableObjectInput(is);
					final boolean hasStreaming = soi.readBoolean();
					if (hasStreaming){
						soi.readStreamBegin();
						return new OutputClientStreamIterable(soi);
					}
					return new OutputClientStreamEmptyIterable();
				}catch(final Throwable t){
					EntityUtils.consume(entity);
					throw t;
				}
			}
			throw new ProtocolException(statusMessage);
		} catch(final Throwable t) {
			if (response != null) {
				response.close();
			}
			throw t;
		}
	}

	private static final boolean isStreamCall(final Method method){
		final Class<?>[] argTypes = method.getParameterTypes();
		final Class<?> returnType = method.getReturnType();
		return returnType.isAssignableFrom(StreamIterable.class)
				&& argTypes.length > 0
				&& argTypes[argTypes.length -1].isAssignableFrom(StreamIterable.class);
	}

	protected static void addRequestHeaders(final HttpPost post) {
		post.addHeader("Content-Type", ProtocolConstants.CONTENT_TYPE);
		//post.addHeader("Accept-Encoding", "deflate");
		// final String basicAuth = _factory.getBasicAuth();
		// if (basicAuth != null) {
		// conn.addHeader("Authorization", basicAuth);
		// }
	}
}
