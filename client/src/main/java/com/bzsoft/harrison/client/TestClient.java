package com.bzsoft.harrison.client;

import java.util.ArrayList;
import java.util.List;

import com.bzsoft.harrison.proto.ClientUtil;
import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.service.TestService;

public class TestClient {


	public static void main(final String[] args) throws Exception {
		final String url ="http://localhost:8080/services/test";
		final Class<TestService> apiClass = TestService.class;
		final ServiceProxyFactory f = new ClientProxyFactory();
		final TestService service = f.create(apiClass, url);
		//		for(int i = 0; i < 10; i++){
		//			final int a = service.compute(String.valueOf(i), "500");
		//			System.out.println(a);
		//		}
		final List<String> list = new ArrayList<String>();
		for(int i = 0; i < 1000; i++){
			list.add(String.valueOf(i));
		}
		final StreamIterable<String> si = ClientUtil.createStreamIterable(list);
		service.streamUp("a", "b", si);
		System.out.println("UP!");

		StreamIterable<String> r = null;
		try{
			r = service.streamDown("a", "b", null);
			for(final String s : r){
				System.out.print(s+" ");
			}
		}finally{
			ProtocolUtil.close(r);
		}
	}
}
