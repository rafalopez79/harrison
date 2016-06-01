package com.bzsoft.harrison.proto;

import java.util.Iterator;

public class IterableIterator<T> implements Iterable<T> {

    private final Iterator<T> iterator;

    private IterableIterator(final Iterator<T> iterator) {
        this.iterator = iterator;
    }

    public static <T> Iterable<T> of(final Iterator<T> iterator) {
        return new IterableIterator<T>(iterator);
    }

    @Override
    public Iterator<T> iterator() {
        return iterator;
    }

}
