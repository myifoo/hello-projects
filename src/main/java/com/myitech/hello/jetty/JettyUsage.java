package com.myitech.hello.jetty;

import org.eclipse.jetty.server.Server;

import java.io.FileNotFoundException;

/**
 * Created by A.T on 2018/1/24.
 *
 * 这个接口就是列举了有哪些可用的方法;
 */
public interface JettyUsage {
    Server defaultServer();
    Server getHandlerListUsageServer();
    Server getHandlerCollectionUsageServer();
    Server getConnectorUsageServer();
    Server getSslConnectorUsageServer() throws FileNotFoundException;
    Server getServletUsageServer();
    Server getContextHandlerUsageServer();
    Server getServletContextUsageServer();
    Server getWebAppContextUsageServer();
    Server getJettyConfigurationUsageServer() throws FileNotFoundException;
    Server getFilterInServletHandlerUsageServer();
    Server getFilterInServletContextUsageServer();
}
