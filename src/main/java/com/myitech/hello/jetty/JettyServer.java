package com.myitech.hello.jetty;

import com.myitech.hello.jetty.handlers.HelloHandler;
import com.myitech.hello.jetty.handlers.SecondHandler;
import com.myitech.hello.jetty.filters.HelloFilter;
import com.myitech.hello.jetty.servlets.HelloServlet;
import org.eclipse.jetty.deploy.DeploymentManager;
import org.eclipse.jetty.deploy.PropertiesConfigurationManager;
import org.eclipse.jetty.deploy.bindings.DebugListenerBinding;
import org.eclipse.jetty.deploy.providers.WebAppProvider;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.rewrite.handler.RewriteHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.handler.*;
import org.eclipse.jetty.servlet.*;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ScheduledExecutorScheduler;
import org.eclipse.jetty.webapp.Configuration;
import org.eclipse.jetty.webapp.WebAppContext;

import javax.servlet.DispatcherType;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.management.ManagementFactory;
import java.util.EnumSet;

/**
 * Created by A.T on 2018/1/24.
 */
public class JettyServer implements JettyUsage {

    Server server;

    public JettyServer() {
        this.server = new Server(8080);
    }

    public Server defaultServer() {
        HandlerList handlers = new HandlerList();

        /**
         * 1. HandlerList 包含ResourceHandler 和DefaultHandler，DefaultHandler 将会为不能匹配到资源的生成一个格式良好的404回应。
         * 3. 一旦ResouceHandler已经匹配并处理了request，后续的Handler将不再被调用。
         */
        handlers.setHandlers(new Handler[] {new HelloHandler(), new SecondHandler()});
        server.setHandler(handlers);

        return server;
    }

    public Server getHandlerListUsageServer() {

        /**
         *  一个包含多个处理器的集合，按照顺序处理，与HandlerCollection不同的是，
         *  当处理器出现异常或者响应被提交或者request.isHandled()方法返回true时，后续将不再被调用。
         *  一般用来匹配不同的主机，用来进行不同的处理。
         */
        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] {new HelloHandler(), new SecondHandler()});
        server.setHandler(handlers);

        return server;
    }

    /**
     * SecondHandler will not work. Because baseRequest.setHandled(true) is called in HelloHandler!
     */
    public Server getHandlerCollectionUsageServer() {
        /**
         * 一个包含多个处理器的集合，按照顺序依次处理。这在响应请求的同时进行统计和日志记录特别有用。
         */
        HandlerCollection handlers = new HandlerCollection();
        handlers.setHandlers(new Handler[] {new HelloHandler(), new SecondHandler()});
        server.setHandler(handlers);

        return server;
    }

    public Server getConnectorUsageServer() {
        Server _server = new Server();

        /**
         * 1. 通过ServerConnector实现对传输层的配置；
         * 2. 可以实现多连接(HTTP, HTTPS)的Server
         *
         */
        ServerConnector http = new ServerConnector(_server);
        http.setHost("localhost");
        http.setPort(8080);
        http.setIdleTimeout(30000);

        _server.addConnector(http);

        _server.setHandler(new HelloHandler());

        return _server;
    }

    public Server getSslConnectorUsageServer() throws FileNotFoundException {
        //这个例子会展示如何配置SSL，我们需要一个秘钥库，会在jetty.home下面找
        String jettyDistKeystore = "../../jetty-distribution/target/distribution/demo-base/etc/keystore";
        String keystorePath = System.getProperty("example.keystore", jettyDistKeystore);
        File keystoreFile = new File(keystorePath);
        if (!keystoreFile.exists()) {
            throw new FileNotFoundException(keystoreFile.getAbsolutePath());
        }

        //创建一个不指定端口的Server，随后将直接配置连接和端口
        Server server = new Server();

        //HTTP配置
        //HttpConfiguration是一个配置http和https属性的集合，默认的配置是http的
        //带secured的ui配置https的，
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);

        //HTTP连接
        //第一个创建的连接是http连接，传入刚才创建的配置信息，也可以重新设置新的配置，如端口，超时等
        ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);

        //使用SslContextFactory来创建http
        //SSL需要一个证书，所以我们配置一个工厂来获得需要的东西
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(keystoreFile.getAbsolutePath());
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");

        //HTTPS的配置类
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        SecureRequestCustomizer src = new SecureRequestCustomizer();
        src.setStsMaxAge(2000);
        src.setStsIncludeSubDomains(true);
        https_config.addCustomizer(src);

        //HTTPS连接
        //创建第二个连接，
        ServerConnector https = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory, HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        https.setPort(8443);
        https.setIdleTimeout(500000);

        // 设置一个连接的集合
        server.setConnectors(new Connector[] { http, https });

        // 设置一个处理器
        server.setHandler(new HelloHandler());
        return null;
    }

    public Server getServletUsageServer() {
        ServletHandler handler = new ServletHandler();
        server.setHandler(handler);

        handler.addServletWithMapping(HelloServlet.class, "/hello/*");
        return server;
    }

    /**
     * 1. ContextHandler是一种ScopedHandler，只用来响应配匹配指定 URI前缀 的请求;
     * 2.
     */
    public Server getContextHandlerUsageServer() {
        ContextHandler context = new ContextHandler("/hello");
        context.setHandler(new HelloHandler());

        ContextHandler contextSecond = new ContextHandler("/second");
        contextSecond.setHandler(new SecondHandler());

        ContextHandlerCollection contexts = new ContextHandlerCollection();
        contexts.setHandlers(new Handler[]{context, contextSecond});

        server.setHandler(contexts);

        return server;
    }


    /**
     * ServletContextHandler是一种特殊的ContextHandler，它可以支持标准的sessions 和Servlets。
     * 下面例子的OneServletContext 实例化了一个 DefaultServlet为/tmp/
     */
    public Server getServletContextUsageServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        context.setResourceBase(System.getProperty("java.io.tmpdir"));
        server.setHandler(context);

        // 增加一个默认的servlet
        context.addServlet(DefaultServlet.class, "/");

        return server;
    }


    /**
     * WebAppContext是ServletContextHandler 的扩展，使用标准的web应用组件和web.xml，通过web.xml和注解配置servlet，filter和其它特性。
     */
    public Server getWebAppContextUsageServer() {
        //下面这个web应用是一个完整的web应用，在这个例子里设置/为根路径，web应用所有的配置都是有效的，
        WebAppContext webapp = new WebAppContext();
        webapp.setContextPath("/");
        File warFile = new File("../../jetty-distribution/target/distribution/test/webapps/test/");
        webapp.setWar(warFile.getAbsolutePath());
        webapp.addAliasCheck(new AllowSymLinkAliasChecker());
        return server;
    }

    /**
     * 通常配置Jetty实例是通过配置jetty.xml和其它关联的配置文件，然而Jetty 配置信息都可以用简单的代码进行配置，下面的例子将从配置文件中获得相关信息：
     * jetty.xml
     * jetty-jmx.xml
     * jetty-http.xml
     * jetty-https.xml
     * jetty-deploy.xml
     * jetty-stats.xml
     * jetty-requestlog.xml
     * jetty-lowresources.xml
     * test-realm.xml
     */
    public Server getJettyConfigurationUsageServer() throws FileNotFoundException {
        // Path to as-built jetty-distribution directory
        String jettyHomeBuild = "../../jetty-distribution/target/distribution";

        // Find jetty home and base directories
        String homePath = System.getProperty("jetty.home", jettyHomeBuild);
        File start_jar = new File(homePath,"start.jar");
        if (!start_jar.exists())
        {
            homePath = jettyHomeBuild = "jetty-distribution/target/distribution";
            start_jar = new File(homePath,"start.jar");
            if (!start_jar.exists())
                throw new FileNotFoundException(start_jar.toString());
        }

        File homeDir = new File(homePath);

        String basePath = System.getProperty("jetty.base", homeDir + "/demo-base");
        File baseDir = new File(basePath);
        if(!baseDir.exists())
        {
            throw new FileNotFoundException(baseDir.getAbsolutePath());
        }

        // Configure jetty.home and jetty.base system properties
        String jetty_home = homeDir.getAbsolutePath();
        String jetty_base = baseDir.getAbsolutePath();
        System.setProperty("jetty.home", jetty_home);
        System.setProperty("jetty.base", jetty_base);


        // === jetty.xml ===
        // Setup Threadpool
        QueuedThreadPool threadPool = new QueuedThreadPool();
        threadPool.setMaxThreads(500);

        // Server
        Server _server = new Server(threadPool);

        // Scheduler
        _server.addBean(new ScheduledExecutorScheduler());

        // HTTP Configuration
        HttpConfiguration http_config = new HttpConfiguration();
        http_config.setSecureScheme("https");
        http_config.setSecurePort(8443);
        http_config.setOutputBufferSize(32768);
        http_config.setRequestHeaderSize(8192);
        http_config.setResponseHeaderSize(8192);
        http_config.setSendServerVersion(true);
        http_config.setSendDateHeader(false);
        // httpConfig.addCustomizer(new ForwardedRequestCustomizer());

        // Handler Structure
        HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
        handlers.setHandlers(new Handler[] { contexts, new DefaultHandler() });
        server.setHandler(handlers);

        // Extra options
        server.setDumpAfterStart(false);
        server.setDumpBeforeStop(false);
        server.setStopAtShutdown(true);

        // === jetty-jmx.xml ===
        MBeanContainer mbContainer = new MBeanContainer(
                ManagementFactory.getPlatformMBeanServer());
        server.addBean(mbContainer);


        // === jetty-http.xml ===
        ServerConnector http = new ServerConnector(server,
                new HttpConnectionFactory(http_config));
        http.setPort(8080);
        http.setIdleTimeout(30000);
        server.addConnector(http);


        // === jetty-https.xml ===
        // SSL Context Factory
        SslContextFactory sslContextFactory = new SslContextFactory();
        sslContextFactory.setKeyStorePath(jetty_home + "/../../../jetty-server/src/test/config/etc/keystore");
        sslContextFactory.setKeyStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setKeyManagerPassword("OBF:1u2u1wml1z7s1z7a1wnl1u2g");
        sslContextFactory.setTrustStorePath(jetty_home + "/../../../jetty-server/src/test/config/etc/keystore");
        sslContextFactory.setTrustStorePassword("OBF:1vny1zlo1x8e1vnw1vn61x8g1zlu1vn4");
        sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA",
                "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA",
                "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
                "SSL_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA",
                "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");

        // SSL HTTP Configuration
        HttpConfiguration https_config = new HttpConfiguration(http_config);
        https_config.addCustomizer(new SecureRequestCustomizer());

        // SSL Connector
        ServerConnector sslConnector = new ServerConnector(server,
                new SslConnectionFactory(sslContextFactory,HttpVersion.HTTP_1_1.asString()),
                new HttpConnectionFactory(https_config));
        sslConnector.setPort(8443);
        server.addConnector(sslConnector);


        // === jetty-deploy.xml ===
        DeploymentManager deployer = new DeploymentManager();
        DebugListener debug = new DebugListener(System.out,true,true,true);
        server.addBean(debug);
        deployer.addLifeCycleBinding(new DebugListenerBinding(debug));
        deployer.setContexts(contexts);
        deployer.setContextAttribute(
                "org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern",
                ".*/servlet-api-[^/]*\\.jar$");

        WebAppProvider webapp_provider = new WebAppProvider();
        webapp_provider.setMonitoredDirName(jetty_base + "/webapps");
        webapp_provider.setDefaultsDescriptor(jetty_home + "/etc/webdefault.xml");
        webapp_provider.setScanInterval(1);
        webapp_provider.setExtractWars(true);
        webapp_provider.setConfigurationManager(new PropertiesConfigurationManager());

        deployer.addAppProvider(webapp_provider);
        server.addBean(deployer);

        // === setup jetty plus ==
        Configuration.ClassList.setServerDefault(server).addAfter(
                "org.eclipse.jetty.webapp.FragmentConfiguration",
                "org.eclipse.jetty.plus.webapp.EnvConfiguration",
                "org.eclipse.jetty.plus.webapp.PlusConfiguration");

        // === jetty-stats.xml ===
        StatisticsHandler stats = new StatisticsHandler();
        stats.setHandler(server.getHandler());
        server.setHandler(stats);
//        ConnectorStatistics.addToAllConnectors(server);

        // === Rewrite Handler
        RewriteHandler rewrite = new RewriteHandler();
        rewrite.setHandler(server.getHandler());
        server.setHandler(rewrite);

        // === jetty-requestlog.xml ===
        NCSARequestLog requestLog = new NCSARequestLog();
        requestLog.setFilename(jetty_home + "/logs/yyyy_mm_dd.request.log");
        requestLog.setFilenameDateFormat("yyyy_MM_dd");
        requestLog.setRetainDays(90);
        requestLog.setAppend(true);
        requestLog.setExtended(true);
        requestLog.setLogCookies(false);
        requestLog.setLogTimeZone("GMT");
        RequestLogHandler requestLogHandler = new RequestLogHandler();
        requestLogHandler.setRequestLog(requestLog);
        handlers.addHandler(requestLogHandler);


        // === jetty-lowresources.xml ===
        LowResourceMonitor lowResourcesMonitor=new LowResourceMonitor(server);
        lowResourcesMonitor.setPeriod(1000);
        lowResourcesMonitor.setLowResourcesIdleTimeout(200);
        lowResourcesMonitor.setMonitorThreads(true);
        lowResourcesMonitor.setMaxConnections(0);
        lowResourcesMonitor.setMaxMemory(0);
        lowResourcesMonitor.setMaxLowResourcesTime(5000);
        server.addBean(lowResourcesMonitor);


        // === test-realm.xml ===
        HashLoginService login = new HashLoginService();
        login.setName("Test Realm");
        login.setConfig(jetty_base + "/etc/realm.properties");
        login.setHotReload(false);
        server.addBean(login);

        return server;
    }

    public Server getFilterInServletContextUsageServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/app");

        context.addServlet(HelloServlet.class, "/hello/*");
        context.addFilter(HelloFilter.class, "/hello/filter", EnumSet.of(DispatcherType.REQUEST));

        server.setHandler(context);

        return server;
    }

    public Server getFilterInServletHandlerUsageServer() {
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/app");

        ServletHandler handler = new ServletHandler();

        handler.addServletWithMapping(HelloServlet.class, "/hello/*");
        handler.addFilterWithMapping(HelloFilter.class, "/hello/filter", EnumSet.of(DispatcherType.REQUEST));

        context.setServletHandler(handler);
        server.setHandler(context);
        return server;
    }
}
