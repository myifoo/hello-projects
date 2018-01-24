package com.myitech.hello.jetty;

import org.eclipse.jetty.server.Server;

/**
 * Created by A.T on 2018/1/24.
 *
 * 本项目中的代码大量参考了 https://www.cnblogs.com/yiwangzhibujian/p/5845623.html
 *
 */
public class Main {
    public static void main(String[] args) {
        Server server = new JettyServer().getFilterInServletHandlerUsageServer();

        try {
            server.start();
            server.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
