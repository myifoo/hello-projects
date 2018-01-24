package com.myitech.hello.guice.modules;

import com.google.inject.Scopes;
import com.google.inject.servlet.GuiceFilter;
import com.myitech.hello.guice.JettyWebServer;
import com.myitech.hello.guice.resources.HelloResource;
import com.sun.jersey.guice.JerseyServletModule;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * Created by A.T on 2018/1/24.
 */
public class ServletModule extends JerseyServletModule {

    @Override
    protected void configureServlets() {
        binder().requireExplicitBindings();
        bind(GuiceFilter.class);
        bind(GuiceContainer.class);
        bind(JettyWebServer.class);
        bind(HelloResource.class).in(Scopes.SINGLETON);

        serve("/*").with(GuiceContainer.class);
    }
}
