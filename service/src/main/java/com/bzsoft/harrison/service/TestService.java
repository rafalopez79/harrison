package com.bzsoft.harrison.service;

import com.bzsoft.harrison.proto.StreamIterable;

public interface TestService {

	public int compute(String a, String b);

	public StreamIterable<String> streamUp(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamDown(String a, String b, StreamIterable<String> stream);

	public StreamIterable<String> streamUpDown(String a, String b, StreamIterable<String> stream);


}
