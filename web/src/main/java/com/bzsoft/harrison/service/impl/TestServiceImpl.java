package com.bzsoft.harrison.service.impl;

import java.math.BigDecimal;

import com.bzsoft.harrison.proto.StreamIterable;
import com.bzsoft.harrison.service.DataDTO;
import com.bzsoft.harrison.service.DataPO;
import com.bzsoft.harrison.service.TestService;

public class TestServiceImpl implements TestService {

	private static final int COUNT = 10000;

	@Override
	public int compute(final String a, final String b) {
		return Integer.parseInt(a) + Integer.parseInt(b);
	}

	@Override
	public StreamIterable<String> streamDown(final String a, final String b, final StreamIterable<String> stream) {
		try {
			System.out.println("StreamDown " + a + "," + b);
			for (int i = 0; i < COUNT; i++) {
				stream.write(String.valueOf(i));
			}
			return stream;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public String streamUp(final String a, final String b, final StreamIterable<String> stream) {
		System.out.println("StreamUp! " + a + "," + b);
		for (final String item : stream) {
			System.out.print(item + " ");
		}
		return "All right chicago";
	}

	@Override
	public StreamIterable<String> streamUpDown(final String a, final String b, final StreamIterable<String> stream) {
		try {
			System.out.println("StreamUpDown " + a + "," + b);
			for (final String s : stream) {
				System.out.println(s);
			}
			for (int i = 0; i < 10; i++) {
				stream.write(String.valueOf(i));
			}
			return null;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public StreamIterable<DataDTO> dataDown(final DataPO po, final StreamIterable<DataDTO> stream) {
		try {
			System.out.println("dataDown " + po);
			for (int i = 0; i < COUNT; i++) {
				final BigDecimal price = new BigDecimal(i);
				final DataDTO dto = new DataDTO((byte)i, price, i);
				System.out.println(dto);
				stream.write(dto);
			}
			return stream;
		} catch (final Exception e) {
			throw new RuntimeException(e);
		}
	}
}
