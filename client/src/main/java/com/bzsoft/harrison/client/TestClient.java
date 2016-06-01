package com.bzsoft.harrison.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bzsoft.harrison.proto.ClientUtil;
import com.bzsoft.harrison.proto.ProtocolConstants;
import com.bzsoft.harrison.proto.ProtocolUtil;
import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.service.TestService;

public class TestClient {

    private static final class ErrorIterator implements Iterator<String> {

        int i = 0;

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext() {
            return i < 1000;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String next() {
            if (i < 100) {
                i++;
                final String s = String.valueOf(i);
                return s;
            }
            throw new RuntimeException();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

    }

    public static void main(final String[] args) throws Exception {
        final String url = "http://localhost:8080/services/test";
        final Class<TestService> apiClass = TestService.class;
        final ServiceProxyFactory f = new ClientProxyFactory(ProtocolConstants.KRYO_SERIALIZATION);
        final TestService service = f.create(apiClass, url);
        // for(int i = 0; i < 10; i++){
        // final int a = service.compute(String.valueOf(i), "500");
        // System.out.println(a);
        // }

        final List<String> list = new ArrayList<String>();
        for (int i = 0; i < 100; i++) {
            list.add(String.valueOf(i));
        }
        // try {
        // final StreamIterable<String> si = ClientUtil.createStreamIterable(IterableIterator.of(new ErrorIterator()));
        // final String result = service.streamUp("a", "b", si);
        // } catch (final Exception e) {
        // e.printStackTrace();
        // }
        // {
        // final StreamIterable<String> si = ClientUtil.createStreamIterable(list);
        // final String result = service.streamUp("a", "b", si);
        // System.out.println("RESULT UP: " + result);
        // System.out.println("UP!");
        //
        // StreamIterable<String> r = null;
        // try {
        // r = service.streamDown("a", "b", null);
        // for (final String s : r) {
        // System.out.print(s + " ");
        // }
        // } finally {
        // ProtocolUtil.close(r);
        // }
        // }
        {
            final StreamIterable<String> si = ClientUtil.createStreamIterable(list);
            StreamIterable<String> result = null;
            try {
                result = service.streamUpDown("a", "b", si);
                for (final String s : result) {
                    System.out.print(s + " ");
                }
            } finally {
                ProtocolUtil.close(result);
            }

        }
    }
}
