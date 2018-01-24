package com.myitech.hello.jetty.filters;

import javax.servlet.*;
import java.io.IOException;
import java.util.Enumeration;

/**
 * Created by A.T on 2018/1/24.
 */
public class HelloFilter implements Filter{
    public void init(FilterConfig filterConfig) throws ServletException {
        Enumeration<String> enums = filterConfig.getInitParameterNames();

        while (enums.hasMoreElements()) {
            String param = (String) enums.nextElement();
            System.out.println(param + ":" + filterConfig.getInitParameter(param));
        }
    }

    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletResponse.getWriter().println("<p>Hello Filter!</p>");
        filterChain.doFilter(servletRequest,servletResponse);
    }

    public void destroy() {
        System.out.println("Destroy HelloFilter!");
    }
}
