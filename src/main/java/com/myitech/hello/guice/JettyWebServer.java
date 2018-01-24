package com.myitech.hello.guice;

import com.google.inject.servlet.GuiceFilter;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.DefaultServlet;
import org.eclipse.jetty.servlet.ServletContextHandler;

import java.net.InetSocketAddress;

/**
 * Created by A.T on 2018/1/24.
 */
public class JettyWebServer {
    public void start() {
        /**
         * 1. In jetty, everything is handler!
         * 2. Different handlers with different levels: Server > ServletContext > Filter, Servlet, Session, Security ...
         */
        Server webserver = new Server(new InetSocketAddress("localhost", 8080));

        /**
         * 1. Handler is jetty's core concept, {@link Handler};
         * 2. handlers are passed the servlet API request and response object, but are  not Servlets.
         * 3. ServletContextHandler, ServletHandler
         */
        ServletContextHandler servletContextHandler = new ServletContextHandler();

        /**
         * 1. GuiceFilter is a servlet filter;
         * 2. Typically, only need to register this filter and register any other filters&servlets in ServletModule;
         * 3. GuiceFilter will dispatch requests to injectable filters and servlets:
         */
        servletContextHandler.addFilter(GuiceFilter.class, "/api/*", null);
        servletContextHandler.addServlet(DefaultServlet.class, "/api/*");

        /**
         * ResourceHandler will serve static content and handle If-Modified-Since headers. {@link ResourceHandler}
         */
        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setDirectoriesListed(true);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});
        resourceHandler.setResourceBase("webroot");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{servletContextHandler, resourceHandler, new DefaultHandler()});



        webserver.setHandler(handlers);

        try {
            webserver.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
