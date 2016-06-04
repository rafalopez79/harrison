package com.bzsoft.harrison.proto;

import java.io.IOException;
import java.io.Serializable;
import java.util.Iterator;

import com.bzsoft.harrison.proto.stream.SerializableObjectOutput;

public class OutputServerStreamIterable<T extends Serializable> implements StreamIterable<T> {

	private final SerializableObjectOutput soi;
	private int count;

	public OutputServerStreamIterable(final SerializableObjectOutput soi) {
		this.soi = soi;
		count = 0;
	}

	@Override
	public void close(){
		//empty
	}

	@Override
	public Iterator<T> iterator() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void cancel()  {
		close();
	}

	@Override
	public void write(final T item)  {
		try{
			soi.writeBoolean(true);
			soi.writeObject(item);
			if (++count  == ProtocolConstants.RESET_COUNT){
				soi.reset();
				count = 0;
			}
		}catch(final IOException e){
			throw new ProtocolRuntimeException(e);
		}
	}

}
