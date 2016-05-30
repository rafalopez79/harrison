package com.bzsoft.harrison.server.spring;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

import com.bzsoft.harrison.proto.ProtocolConstants;

public class HarrisonServiceExporter extends HarrisonExporter implements HttpRequestHandler {


	@Override
	public void handleRequest(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(), new String[] { "POST" },
					"HarrisonServiceExporter only supports POST requests");
		}
		response.setContentType(ProtocolConstants.CONTENT_TYPE);
		try {
			invoke(request.getInputStream(), response.getOutputStream());
		} catch (final Throwable ex) {
			throw new NestedServletException("Harrison skeleton invocation failed", ex);
		}
	}
}
