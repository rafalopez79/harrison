package com.bzsoft.harrison.proto.stream;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;

public interface SerializableObjectOutput extends Closeable, Flushable {

    public void writeString(String s) throws IOException;

    public void writeResult(Object o, Throwable t) throws IOException;

    public void reset() throws IOException;

    public void writeStreamBegin() throws IOException;

    public void writeStreamEnd() throws IOException;

    public void writeBoolean(boolean b) throws IOException;

    public void writeObject(Object o) throws IOException;

    public void writeInt(int i) throws IOException;

    public void writeUTF(String s) throws IOException;

}
