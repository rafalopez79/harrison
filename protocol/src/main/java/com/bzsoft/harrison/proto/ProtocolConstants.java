package com.bzsoft.harrison.proto;

public final class ProtocolConstants {

	private ProtocolConstants() {
		// empty
	}

	public static final int MAGIC = 0x0FE0BEBE;
	public static final int VERSION = 0x00000000;
	public static final byte JAVA_SERIALIZATION = 0;
	public static final byte KRYO_SERIALIZATION = 1;

	public static final byte STANDARD_CALL = 'C';
	public static final byte UPLOAD_CALL = 'U';
	public static final byte DOWNLOAD_CALL = 'D';
	public static final byte UPLOADDOWNLOAD_CALL = 'X';

	public static final byte STREAM_BEGINING = 'B';
	public static final byte STREAM_END = 'E';

	public static final String PROTOCOL_NAME = "Harrison(tm)";
	public static final String CONTENT_TYPE = "application/harrison";

	public static final int RESET_COUNT = 50;
}
