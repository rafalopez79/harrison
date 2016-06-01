package com.bzsoft.harrison.proto.stream;

import java.io.Closeable;
import java.io.IOException;

public interface SerializableObjectInput extends Closeable {

    public String readString() throws IOException;

    public Object readResult() throws Throwable;

    public void readStreamBegin() throws IOException;

    public void readStreamEnd() throws IOException;

    public boolean readBoolean() throws IOException;

    public Object readObject() throws IOException, ClassNotFoundException;

    public int readInt() throws IOException;

    public String readUTF() throws IOException;

}
