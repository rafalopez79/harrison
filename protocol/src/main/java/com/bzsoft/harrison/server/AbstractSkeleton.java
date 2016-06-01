package com.bzsoft.harrison.server;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The Class AbstractSkeleton.
 */
public abstract class AbstractSkeleton {

    protected final Class<?> apiClass;
    protected final Map<String, Method> methodMap;

    protected AbstractSkeleton(final Class<?> apiClass) {
        this.apiClass = apiClass;
        methodMap = new HashMap<String, Method>();
        final Method[] methodList = apiClass.getMethods();
        for (final Method method : methodList) {
            methodMap.put(method.toGenericString(), method);
        }
    }

    /**
     * Returns the API class of the current object.
     * 
     * @return the API class name
     */
    public String getAPIClassName() {
        return apiClass.getName();
    }

    /**
     * Returns the method by the mangled name.
     * 
     * @param mangledName
     *            the name passed by the protocol
     */
    protected Method getMethod(final String mangledName) {
        return methodMap.get(mangledName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[" + apiClass.getName() + "]";
    }
}
