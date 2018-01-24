package com.myitech.hello.guice;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.myitech.hello.guice.modules.ServletModule;

/**
 * Created by A.T on 2018/1/24.
 */
public class Main {
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        Injector injector = Guice.createInjector(new ServletModule());

        injector.getInstance(JettyWebServer.class).start();
    }
}
