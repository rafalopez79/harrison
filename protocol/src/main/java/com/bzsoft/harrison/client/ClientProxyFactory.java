package com.bzsoft.harrison.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.WinHttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactoryCreator;
import com.bzsoft.harrison.proto.stream.SerializerType;
import com.bzsoft.harrison.proto.stream.impl.SerializableObjectStreamFactoryCreatorImpl;

/**
 * A factory for creating ClientProxy objects.
 */
public class ClientProxyFactory implements ServiceProxyFactory {

	protected static final Logger LOGGER = LoggerFactory.getLogger(ClientProxyFactory.class);

	private final ClassLoader loader;

	private boolean isChunkedPost = true;

	private long readTimeout = -1;
	private long connectTimeout = -1;

	private final boolean compress;
	private final boolean spnego;
	private final String user;
	private final String password;

	private final SerializerType serializerType;
	private final CloseableHttpClient client;
	private final SerializableObjectStreamFactoryCreator creator;

	/**
	 * Creates the new proxy factory.
	 */
	public ClientProxyFactory(final SerializerType serializerType, final boolean spnego,
			final String user, final String password, final boolean compress) {
		this(Thread.currentThread().getContextClassLoader(), serializerType, spnego, user, password, compress);
	}

	/**
	 * Creates the new proxy factory.
	 *
	 * @param loader
	 *            the loader
	 */
	public ClientProxyFactory(final ClassLoader loader, final SerializerType serializerType, final boolean spnego,
			final String user, final String password, final boolean compress) {
		creator = new SerializableObjectStreamFactoryCreatorImpl();
		this.loader = loader;
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		if (spnego){
			client = WinHttpClients.custom().setConnectionManager(cm).build();
		}else{
			client = HttpClients.custom().setConnectionManager(cm).build();
		}
		this.compress = compress;
		this.spnego = spnego;
		this.user = user;
		this.password = password;
		this.serializerType = serializerType;
	}



	/**
	 * Set true if should use chunked encoding on the request.
	 *
	 * @param isChunked
	 *            the new chunked post
	 */
	public void setChunkedPost(final boolean isChunked) {
		isChunkedPost = isChunked;
	}

	/**
	 * Set true if should use chunked encoding on the request.
	 *
	 * @return true, if is chunked post
	 */
	public boolean isChunkedPost() {
		return isChunkedPost;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 *
	 * @return the read timeout
	 */
	public long getReadTimeout() {
		return readTimeout;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 *
	 * @param timeout
	 *            the new read timeout
	 */
	public void setReadTimeout(final long timeout) {
		readTimeout = timeout;
	}

	/**
	 * The socket connection timeout in milliseconds.
	 *
	 * @return the connect timeout
	 */
	public long getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * The socket connect timeout in milliseconds.
	 *
	 * @param timeout
	 *            the new connect timeout
	 */
	public void setConnectTimeout(final long timeout) {
		connectTimeout = timeout;
	}


	public SerializerType getSerializationType() {
		return serializerType;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T> T create(final Class<T> api, final String urlName) throws MalformedURLException {
		return create(api, urlName, loader);
	}

	/**
	 * Crea el.
	 *
	 * @param <T>
	 *            the generic type
	 * @param api
	 *            the api
	 * @param urlName
	 *            the url name
	 * @param cl
	 *            the cl
	 * @return the t
	 * @throws MalformedURLException
	 *             the malformed url exception
	 */
	public <T> T create(final Class<T> api, final String urlName, final ClassLoader cl) throws MalformedURLException {
		final URL url = new URL(urlName);
		return create(api, url, cl);
	}

	/**
	 * Crea el.
	 *
	 * @param <T>
	 *            the generic type
	 * @param api
	 *            the api
	 * @param url
	 *            the url
	 * @param cl
	 *            the cl
	 * @return the t
	 */
	@SuppressWarnings("unchecked")
	public <T> T create(final Class<T> api, final URL url, final ClassLoader cl) {
		if (api == null) {
			throw new NullPointerException("api must not be null for ProxyFactory.create()");
		}
		final SerializableObjectStreamFactory sosFactory = creator.create(serializerType);
		final InvocationHandler handler = new ClientProxy(url, api, client, sosFactory, spnego, user, password, compress);
		return (T) Proxy.newProxyInstance(cl, new Class[] { api }, handler);
	}
}
