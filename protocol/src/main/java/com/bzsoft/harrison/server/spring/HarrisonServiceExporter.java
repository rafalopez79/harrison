package com.bzsoft.harrison.server.spring;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.bzsoft.harrison.proto.IgnoreCloseOutputStream;
import com.bzsoft.harrison.proto.ProtocolConstants;

public class HarrisonServiceExporter extends HarrisonExporter implements HttpRequestHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(HarrisonServiceExporter.class);

	@Override
	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" },
					"HarrisonServiceExporter only supports POST requests");
		}
		final String encoding = request.getHeader(ProtocolConstants.CONTENT_ENCODING);
		final String acceptEncoding = request.getHeader(ProtocolConstants.ACCEPT_ENCODING);
		final String contentType = request.getContentType();
		LOGGER.info("Received {} {} ", encoding, contentType);

		response.setContentType(ProtocolConstants.CONTENT_TYPE);
		InputStream is = null;
		OutputStream os = null;
		GZIPOutputStream gzos = null;
		if (encoding != null && encoding.indexOf(ProtocolConstants.GZIP_ENCODING) > -1){
			is = new GZIPInputStream(request.getInputStream());
		}else{
			is = request.getInputStream();
		}
		if (acceptEncoding != null && acceptEncoding.indexOf(ProtocolConstants.GZIP_ENCODING) > -1){
			LOGGER.info("USING GZIP");
			response.setHeader(ProtocolConstants.CONTENT_ENCODING, ProtocolConstants.GZIP_ENCODING);
			gzos = new GZIPOutputStream(new IgnoreCloseOutputStream(response.getOutputStream()));
			os = gzos;
		}else{
			os = response.getOutputStream();
		}
		try {
			invoke(is,os);
		} catch (final Throwable ex) {
			throw new NestedServletException("Harrison skeleton invocation failed", ex);
		}finally{
			if (gzos != null){
				gzos.close();
			}
		}
	}
}
