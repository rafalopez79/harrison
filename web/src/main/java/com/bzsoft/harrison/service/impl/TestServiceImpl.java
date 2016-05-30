package com.bzsoft.harrison.service.impl;

import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.service.TestService;

public class TestServiceImpl implements TestService{

	private static final int COUNT = 100000;

	@Override
	public int compute(final String a, final String b) {
		return Integer.parseInt(a) + Integer.parseInt(b);
	}

	@Override
	public StreamIterable<String> streamDown(final String a, final String b, final StreamIterable<String> stream) {
		try{
			System.out.println("StreamDown "+a+","+b);
			for(int i = 0; i < COUNT; i++){
				stream.write(String.valueOf(i));
			}
			return stream;
		}catch(final Exception e){
			throw new RuntimeException(e);
		}
	}

	@Override
	public StreamIterable<String> streamUp(final String a, final String b, final StreamIterable<String> stream) {
		System.out.println("StreamUp! "+a+","+b);
		for(final String item : stream){
			System.out.print(item+" ");
		}
		return null;
	}

	@Override
	public StreamIterable<String> streamUpDown(final String a, final String b, final StreamIterable<String> stream) {
		System.out.println("StreamUpDown "+a+","+b);
		return null;
	}

}
