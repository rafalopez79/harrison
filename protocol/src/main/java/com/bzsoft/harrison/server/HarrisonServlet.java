package com.bzsoft.harrison.server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bzsoft.harrison.proto.ProtocolConstants;

public class HarrisonServlet extends HttpServlet {

	private static final long	serialVersionUID	= 1L;

	private HarrisonSkeleton				skeleton;

	public HarrisonServlet() {
		//empty
	}

	@Override
	public String getServletInfo() {
		return "Harrison Servlet";
	}

	/**
	 * Initialize the service, including the service object.
	 */
	@Override
	public void init(final ServletConfig config) throws ServletException {
		super.init(config);
		try {
			final String apiName = getInitParameter("api");
			final String implName = getInitParameter("impl");
			if (apiName == null || implName == null){
				throw new ServletException("Misconfigured HarrisonServlet");
			}
			final Class<?> apiClass = loadClass(apiName);
			final Class<?> implClass = loadClass(implName);
			final Object impl = implClass.newInstance();
			skeleton = new HarrisonSkeleton(impl, apiClass);
		} catch (final ServletException e) {
			throw e;
		} catch (final Exception e) {
			throw new ServletException(e);
		}
	}

	private static Class<?> loadClass(final String className) throws ClassNotFoundException {
		final ClassLoader loader = getContextClassLoader();
		if (loader != null) {
			return Class.forName(className, false, loader);
		}
		return Class.forName(className);
	}

	private static ClassLoader getContextClassLoader() {
		return Thread.currentThread().getContextClassLoader();
	}


	/**
	 * Execute a request. The path-info of the request selects the bean. Once the
	 * bean's selected, it will be applied.
	 */
	@Override
	public void service(final ServletRequest request, final ServletResponse response) throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse res = (HttpServletResponse) response;

		if (!req.getMethod().equals("POST")) {
			res.setStatus(500);
			final PrintWriter out = res.getWriter();
			res.setContentType("text/html");
			out.println("<h1>Harrison Requires POST</h1>");
			out.close();
			return;
		}

		try {
			final InputStream is = request.getInputStream();
			final OutputStream os = response.getOutputStream();
			response.setContentType(ProtocolConstants.CONTENT_TYPE);
			invoke(is, os);
		} catch (final RuntimeException e) {
			throw e;
		} catch (final ServletException e) {
			throw e;
		} catch (final Throwable e) {
			throw new ServletException(e);
		}
	}

	protected void invoke(final InputStream is, final OutputStream os) throws Exception {
		skeleton.invoke(is, os);
	}

}
