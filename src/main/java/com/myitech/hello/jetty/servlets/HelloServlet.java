package com.myitech.hello.jetty.servlets;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by A.T on 2018/1/24.
 *
 * 1. Servlets是处理逻辑和HTTP请求的标准方式;
 * 2. Servlets 类似于Jetty的Handler, request对象不可变且不能被修改;
 * 3. 在Jetty中servlet将有ServletHandler进行负责调用(Jetty不仅仅是一个servlet engine);
 *
 */
public class HelloServlet extends HttpServlet{
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println("<h1>Hello Servlet!</h1>");
    }
}
