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

import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactory;
import com.bzsoft.harrison.proto.stream.SerializableObjectStreamFactoryCreator;
import com.bzsoft.harrison.proto.stream.impl.SerializableObjectStreamFactoryCreatorImpl;

/**
 * A factory for creating ClientProxy objects.
 */
public class ClientProxyFactory implements ServiceProxyFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(ClientProxyFactory.class);

    private final ClassLoader loader;

    private String user;
    private String password;
    private String basicAuth;
    private boolean isChunkedPost = true;

    private long readTimeout = -1;
    private long connectTimeout = -1;

    private byte serializationType;
    private final CloseableHttpClient client;
    private final SerializableObjectStreamFactoryCreator creator;

    /**
     * Creates the new proxy factory.
     */
    public ClientProxyFactory(final byte type) {
        this(Thread.currentThread().getContextClassLoader(), type);
    }

    /**
     * Creates the new proxy factory.
     * 
     * @param loader
     *            the loader
     */
    public ClientProxyFactory(final ClassLoader loader, final byte type) {
        creator = new SerializableObjectStreamFactoryCreatorImpl();
        this.loader = loader;
        final PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        client = HttpClients.custom().setConnectionManager(cm).build();
    }

    /**
     * Sets the user.
     * 
     * @param user
     *            the new user
     */
    public void setUser(final String user) {
        this.user = user;
        basicAuth = null;
    }

    /**
     * Sets the password.
     * 
     * @param password
     *            the new password
     */
    public void setPassword(final String password) {
        this.password = password;
        basicAuth = null;
    }

    /**
     * Gets the basic auth.
     * 
     * @return the basic auth
     */
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

    public void setSerializationType(final byte serializationType) {
        this.serializationType = serializationType;
    }

    public byte getSerializationType() {
        return serializationType;
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
        final SerializableObjectStreamFactory sosFactory = creator.create(serializationType);
        final InvocationHandler handler = new ClientProxy(url, api, client, sosFactory);
        return (T) Proxy.newProxyInstance(cl, new Class[] { api }, handler);
    }

    /**
     * Creates the Base64 value.
     */
    private static String base64(final String value) {
        final StringBuilder cb = new StringBuilder(value.length() * 2);
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

    private static char encode(final long d) {
        final long l = d & 0x3f;
        if (l < 26) {
            return (char) (l + 'A');
        } else if (l < 52) {
            return (char) (l + 'a' - 26);
        } else if (l < 62) {
            return (char) (l + '0' - 52);
        } else if (l == 62) {
            return '+';
        } else {
            return '/';
        }
    }

}
