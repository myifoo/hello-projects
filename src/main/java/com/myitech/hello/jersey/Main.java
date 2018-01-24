package com.myitech.hello.jersey;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import java.io.IOException;
import java.net.URI;

/**
 * Created by A.T on 2018/1/24.
 */
public class Main {
    // Base URI the Grizzly HTTP server will listen on
    public static final String BASE_URI = "http://localhost:8080/jersey/";

    /**
     * Starts Grizzly HTTP server exposing JAX-RS resources defined in this application.
     * @return Grizzly HTTP server.
     */
    public static HttpServer startServer() {
        // create a resource config that scans for JAX-RS resources and providers
        // in com.myitech.jersey package
        final ResourceConfig rc = new ResourceConfig().packages("com.myitech.hello.jersey");

        // create and start a new instance of grizzly http server
        // exposing the Jersey application at BASE_URI
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), rc);
    }

    /**
     * Main method.
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        final HttpServer server = startServer();

        // 这里配置静态资源在文件系统中的位置，与URI无关；
        StaticHttpHandler handler = new StaticHttpHandler(new String[]{Class.class.getClass().getResource("/webroot").getPath() });

        //
        //
        // http://localhost:8080/index.html

        /**
         * 这里添加静态资源Handler，这里的URI是独立配置的，跟上边的Jersey Resource URI没有关系，要留意;
         *
         * 访问静态资源：
         *      http://localhost:8080/js/index.js
         *      http://localhost:8080/index.html
         *
         * 访问jersey resource：
         *      http://localhost:8080/jersey/hello
         */
        server.getServerConfiguration().addHttpHandler(handler, new String[]{"/"});


        System.out.println(String.format("Jersey app started with WADL available at "
                + "%sapplication.wadl\nHit enter to stop it...", BASE_URI));
        System.in.read();
        server.shutdownNow();
    }
}