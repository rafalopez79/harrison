
package com.bzsoft.harrison.client;

import java.net.MalformedURLException;


public interface ServiceProxyFactory {

	public <T> T create(Class<T> api, String url) throws MalformedURLException;
}
