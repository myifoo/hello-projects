package com.myitech.hello;

import com.myitech.hello.hk2.*;
import junit.framework.TestCase;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extras.ExtrasUtilities;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.reflections.Reflections;

import javax.inject.Singleton;
import java.util.Set;

/**
 * Created by A.T on 2018/1/23.
 */
public class Hk2Test extends TestCase {

    public void testEventUsage()
    {
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        showEvent(factory.create("locator"));
    }

    public void testServiceLocatorUtilitiesUsage()
    {
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        useServiceLocatorUtilities(factory.create("locator"));
    }

    public void testDynamicConfigurationSimpleUsage()
    {
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        useDynamicConfigurationSimple(factory.create("locator"));
    }

    public void testDynamicConfigurationAutoUsage() {
        ServiceLocatorFactory factory = ServiceLocatorFactory.getInstance();
        useDynamicConfigurationAuto(factory.create("locator"));
    }



    public static void showEvent(ServiceLocator locator) {
        ExtrasUtilities.enableTopicDistribution(locator);

        ServiceLocatorUtilities.addClasses(locator, MyModel.class);
        ServiceLocatorUtilities.addClasses(locator, MySubscriber.class);
        ServiceLocatorUtilities.addOneDescriptor(locator, BuilderHelper.link(MyServiceImpl.class).to(MyService.class).build());

        MyModel model = locator.getService(MyModel.class);
        locator.getService(MySubscriber.class);
        model.publish();
    }

    public static void useServiceLocatorUtilities(ServiceLocator locator) {
        ServiceLocatorUtilities.addClasses(locator, MyModel.class);
        ServiceLocatorUtilities.addOneDescriptor(locator, BuilderHelper.link(MyServiceImpl.class).to(MyService.class).build());

        MyModel model = locator.getService(MyModel.class);
        model.act();
    }

    public static void useDynamicConfigurationSimple(ServiceLocator locator) {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        config.bind(BuilderHelper.link(MyServiceImpl.class).to(MyService.class).build());
        config.bind(BuilderHelper.link(MyModel.class).to(MyModel.class).build());

        config.commit(); // Do not forget this step!!

        MyModel model = locator.getService(MyModel.class);
        model.act();
    }

    public static ServiceLocator useDynamicConfigurationAuto(ServiceLocator locator) {
        DynamicConfigurationService dcs = locator.getService(DynamicConfigurationService.class);
        DynamicConfiguration config = dcs.createDynamicConfiguration();

        Reflections reflections = new Reflections("com.myitech.hk2");

        Set<Class<?>> serviceClasses = reflections.getTypesAnnotatedWith(org.jvnet.hk2.annotations.Service.class);
        System.out.println(serviceClasses);

        Set<Class<?>> contractClasses = reflections.getTypesAnnotatedWith(org.jvnet.hk2.annotations.Contract.class);
        System.out.println(contractClasses);

        for(Class<?> serviceClass:serviceClasses){
            Class<?> linkTo = serviceClass;
            for (Class<?> contractClass : contractClasses) {
                if(contractClass.isAssignableFrom(serviceClass) && contractClass.isInterface() ){
                    linkTo = contractClass;
                    break;
                }
            }
            config.bind( BuilderHelper.link(serviceClass).to(linkTo).in(Singleton.class).build() ); //TODO 这里只实现了最简单的方式，还可以更复杂一些
        }


        config.commit();

        MyModel model = locator.getService(MyModel.class);
        model.act();

        return locator;
    }
}
