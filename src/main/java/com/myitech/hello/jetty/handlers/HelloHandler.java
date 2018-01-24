package com.myitech.hello.jetty.handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Created by A.T on 2018/1/24.
 *
 * 1. Handler {@link org.eclipse.jetty.server.Handler} is required by a {@link Server} to handle incoming HTTP requests.
 * 2. handlers is not servlets. The servlet container is implemented by handlers or context, security, session and servlet that modify the request object
 * before passing it to the next stage of handling.
 * 3. 一些处理器会转发请求到其他处理器（例如： ContextHandlerCollection 会根据路径选择匹配的ContextHandler）
 * 4. 另一些将根据逻辑判断来生成相应的响应（例如：ServletHandler 将把request转发到servlet）
 * 5. 还有的处理器将处理和请求无关的操作（例如：RequestLogHandler 或者StatisticsHandler）
 *
 */
public class HelloHandler extends AbstractHandler {
    /**
     * @param target 目标请求，可以是一个URI或者是一个转发到这的处理器的名字
     * @param baseRequest Jetty自己的没有被包装的请求，一个可变的Jetty请求对象
     * @param request 被filter或者servlet包装的请求，一个不可变的Jetty请求对象
     * @param response 响应，可能被filter或者servlet包装过
     */
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setContentType("text/html charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);

        PrintWriter out = response.getWriter();
        out.println("<h1>Hello Handler!</h1>");
        out.println(String.format("<p> target : %s </p>", target));

        /**
         * 1. 通知Jettyrequest使用此处理器处理请求
         * 2. 提交后，HanderList中的其他handler将不再调用。
         */
        baseRequest.setHandled(true);
    }
}
