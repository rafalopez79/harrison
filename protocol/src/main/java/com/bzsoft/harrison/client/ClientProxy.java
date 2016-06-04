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
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.entity.GzipCompressingEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bzsoft.harrison.proto.CountingHttpEntity;
import com.bzsoft.harrison.proto.OutputClientStreamIterable;
import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolException;
import com.bzsoft.harrison.proto.ProtocolRuntimeException;
import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.proto.stream.SerializableObjectInput;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;

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

	private final boolean compress;
	private final boolean spnego;
	private final String user;
	private final String password;

	public ClientProxy(final URL url, final Class<?> type, final CloseableHttpClient client,
			final SerializableObjectStreamFactory sosFactory,
			final boolean spnego, final String user, final String password, final boolean compress) {
		this.url = url;
		this.type = type;
		this.client = client;
		this.sosFactory = sosFactory;
		this.spnego = spnego;
		this.user = user;
		this.password = password;
		this.compress = compress;
	}

	public URL getURL() {
		return url;
	}

	public Class<?> getType() {
		return type;
	}

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
				return Integer.valueOf(url.hashCode());
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
		final byte callType = getCallType(method, args);
		try {
			if (callType == ProtocolConstants.STANDARD_CALL) {
				LOGGER.info("Standard call {} {} {}", url, mangleName, args);
				return standardCall(client, url, sosFactory, mangleName, args, spnego, user, password, compress);
			}
			LOGGER.info("Stream call {} {} {} {}", callType, url, mangleName, args);
			return streamCall(client, url, sosFactory, mangleName, args, callType, spnego, user, password, compress);
		} catch (final ProtocolException e) {
			throw new ProtocolRuntimeException(e);
		} catch (final Throwable t) {
			throw t;
		} finally {
			// empty
		}
	}

	private static final Object standardCall(final CloseableHttpClient client, final URL url,
			final SerializableObjectStreamFactory sosFactory, final String methodName, final Object[] args,
			final boolean spnego,final String user, final String password, final boolean compress)	throws Throwable {
		CloseableHttpResponse response = null;
		try {
			final HttpPost post = new HttpPost(url.toString());
			final HttpEntity callEntity = new StandardCallEntity(methodName, args, sosFactory);
			final CountingHttpEntity countingCallEntity = new CountingHttpEntity(compress ? new GzipCompressingEntity(callEntity) : callEntity);
			post.setEntity(countingCallEntity);
			if (!spnego && user != null && password != null){
				final UsernamePasswordCredentials creds =  new UsernamePasswordCredentials(user, password);
				post.addHeader(new BasicScheme().authenticate(creds, post, null));
			}
			response = client.execute(post);
			LOGGER.info("Call transferred {} bytes", countingCallEntity.getTransferred());
			final StatusLine sl = response.getStatusLine();
			final int statusCode = sl.getStatusCode();
			final String statusMessage = sl.getReasonPhrase();
			LOGGER.info("Call status {} {}", statusCode, statusMessage);
			final HttpEntity baseEntity = response.getEntity();
			final CountingHttpEntity entity = baseEntity == null ? null : new CountingHttpEntity( baseEntity);
			if (entity != null && entity.isStreaming() && statusCode == 200) {
				try {
					final InputStream is = entity.getContent();
					final SerializableObjectInput soi = sosFactory.createSerializableObjectInput(is);
					final Object result = soi.readResult();
					return result;
				} finally {
					LOGGER.info("Downloaded {} bytes", entity.getTransferred());
					EntityUtils.consume(entity);
				}
			}
			throw new ProtocolException(statusMessage);
		} finally {
			if (response != null) {
				response.close();
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static final Object streamCall(final CloseableHttpClient client, final URL url,
			final SerializableObjectStreamFactory sosFactory, final String methodName, final Object[] args,
			final byte callType, final boolean spnego,final String user, final String password, final boolean compress) throws Throwable {
		CloseableHttpResponse response = null;
		try {
			final HttpPost post = new HttpPost(url.toString());
			final int len = args.length;
			final Object[] mangledArgs = new Object[len - 1];
			System.arraycopy(args, 0, mangledArgs, 0, len - 2);
			final Iterable<?> iterable = (Iterable<?>) args[len - 1];
			if (!spnego && user != null && password != null){
				final UsernamePasswordCredentials creds =  new UsernamePasswordCredentials(user, password);
				post.addHeader(new BasicScheme().authenticate(creds, post, null));
			}
			final HttpEntity callEntity = new StreamCallEntity(methodName, mangledArgs, iterable, callType, sosFactory);
			final CountingHttpEntity countingCallEntity = new CountingHttpEntity(compress ? new GzipCompressingEntity(callEntity): callEntity);
			post.setEntity(countingCallEntity);
			response = client.execute(post);
			LOGGER.info("Call transferred {} bytes", countingCallEntity.getTransferred());
			final StatusLine sl = response.getStatusLine();
			final int statusCode = sl.getStatusCode();
			final String statusMessage = sl.getReasonPhrase();
			LOGGER.info("Streamed Call status {} {}", statusCode, statusMessage);
			final HttpEntity baseEntity = response.getEntity();
			final CountingHttpEntity entity = baseEntity == null ? null : new CountingHttpEntity(baseEntity);
			if (entity != null && entity.isStreaming() && statusCode == 200) {
				try {
					final InputStream is = entity.getContent();
					final SerializableObjectInput soi = sosFactory.createSerializableObjectInput(is);
					final boolean hasStreaming = soi.readBoolean();
					if (hasStreaming && callType == ProtocolConstants.UPLOAD_CALL
							|| !hasStreaming && (callType == ProtocolConstants.DOWNLOAD_CALL || callType == ProtocolConstants.UPLOADDOWNLOAD_CALL)) {
						soi.close();
						throw new ProtocolRuntimeException("Response mismatch, stream expected.");
					}
					if (hasStreaming) {
						soi.readStreamBegin();
						return new OutputClientStreamIterable(soi, entity);
					}
					final Object result = soi.readResult();
					soi.close();
					LOGGER.info("Downloaded {} bytes", entity.getTransferred());
					return result;
				} catch (final Throwable t) {
					EntityUtils.consume(entity);
					throw t;
				}
			}
			throw new ProtocolException(statusMessage);
		} catch (final Throwable t) {
			if (response != null) {
				response.close();
			}
			throw t;
		}
	}

	private static final byte getCallType(final Method method, final Object[] args) {
		final Class<?>[] argTypes = method.getParameterTypes();
		final Class<?> returnType = method.getReturnType();
		final boolean hasStreaming = argTypes.length > 0
				&& argTypes[argTypes.length - 1].isAssignableFrom(StreamIterable.class);
		final boolean hasDownload = returnType.isAssignableFrom(StreamIterable.class);
		if (!hasStreaming) {
			return ProtocolConstants.STANDARD_CALL;
		}
		if (!hasDownload) {
			return ProtocolConstants.UPLOAD_CALL;
		} else if (args[args.length - 1] == null) {
			return ProtocolConstants.DOWNLOAD_CALL;
		} else {
			return ProtocolConstants.UPLOADDOWNLOAD_CALL;
		}
	}

	protected static void addRequestHeaders(final HttpPost post) {
		post.addHeader("Content-Type", ProtocolConstants.CONTENT_TYPE);
		// post.addHeader("Accept-Encoding", "deflate");
		// final String basicAuth = _factory.getBasicAuth();
		// if (basicAuth != null) {
		// conn.addHeader("Authorization", basicAuth);
		// }
	}
}
