package com.bzsoft.harrison.client;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClientProxyFactory implements ServiceProxyFactory {

	protected static final Logger			LOGGER			= LoggerFactory.getLogger(ClientProxyFactory.class);

	private final ClassLoader				loader;

	private String								user;
	private String								password;
	private String								basicAuth;
	private boolean							isChunkedPost	= true;

	private long								readTimeout		= -1;
	private long								connectTimeout	= -1;
	private final CloseableHttpClient	client;

	/**
	 * Creates the new proxy factory.
	 */
	public ClientProxyFactory() {
		this(Thread.currentThread().getContextClassLoader());
	}

	/**
	 * Creates the new proxy factory.
	 */
	public ClientProxyFactory(final ClassLoader loader) {
		this.loader = loader;
		final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		client = HttpClients.custom().setConnectionManager(cm).build();
	}

	/**
	 * Sets the user.
	 */
	public void setUser(final String user) {
		this.user = user;
		basicAuth = null;
	}

	public void setPassword(final String password) {
		this.password = password;
		basicAuth = null;
	}

	public String getBasicAuth() {
		if (basicAuth != null) {
			return basicAuth;
		} else if (user != null && password != null) {
			return "Basic " + base64(user + ":" + password);
		} else {
			return null;
		}
	}

	/**
	 * Set true if should use chunked encoding on the request.
	 */
	public void setChunkedPost(final boolean isChunked) {
		isChunkedPost = isChunked;
	}

	/**
	 * Set true if should use chunked encoding on the request.
	 */
	public boolean isChunkedPost() {
		return isChunkedPost;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 */
	public long getReadTimeout() {
		return readTimeout;
	}

	/**
	 * The socket timeout on requests in milliseconds.
	 */
	public void setReadTimeout(final long timeout) {
		readTimeout = timeout;
	}

	/**
	 * The socket connection timeout in milliseconds.
	 */
	public long getConnectTimeout() {
		return connectTimeout;
	}

	/**
	 * The socket connect timeout in milliseconds.
	 */
	public void setConnectTimeout(final long timeout) {
		connectTimeout = timeout;
	}

	@Override
	public <T> T create(final Class<T> api, final String urlName) throws MalformedURLException {
		return create(api, urlName, loader);
	}

	public <T> T create(final Class<T> api, final String urlName, final ClassLoader cl) throws MalformedURLException {
		final URL url = new URL(urlName);
		return create(api, url, cl);
	}

	@SuppressWarnings("unchecked")
	public <T> T create(final Class<T> api, final URL url, final ClassLoader cl) {
		if (api == null) {
			throw new NullPointerException("api must not be null for ProxyFactory.create()");
		}
		final InvocationHandler handler = new ClientProxy(url, api, client);
		return (T) Proxy.newProxyInstance(cl, new Class[] { api }, handler);
	}

	/**
	 * Creates the Base64 value.
	 */
	private static String base64(final String value) {
		final StringBuilder cb = new StringBuilder();
		int i = 0;
		for (i = 0; i + 2 < value.length(); i += 3) {
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + value.charAt(i + 1);
			chunk = (chunk << 8) + value.charAt(i + 2);
			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append(encode(chunk));
		}

		if (i + 1 < value.length()) {
			long chunk = value.charAt(i);
			chunk = (chunk << 8) + value.charAt(i + 1);
			chunk <<= 8;

			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append(encode(chunk >> 6));
			cb.append('=');
		} else if (i < value.length()) {
			long chunk = value.charAt(i);
			chunk <<= 16;

			cb.append(encode(chunk >> 18));
			cb.append(encode(chunk >> 12));
			cb.append('=');
			cb.append('=');
		}

		return cb.toString();
	}

	private static char encode(long d) {
		d &= 0x3f;
		if (d < 26) {
			return (char) (d + 'A');
		} else if (d < 52) {
			return (char) (d + 'a' - 26);
		} else if (d < 62) {
			return (char) (d + '0' - 52);
		} else if (d == 62) {
			return '+';
		} else {
			return '/';
		}
	}

}
