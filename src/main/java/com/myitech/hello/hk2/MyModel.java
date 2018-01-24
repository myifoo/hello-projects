package com.myitech.hello.hk2;

import org.glassfish.hk2.api.messaging.Topic;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by A.T on 2018/1/23.
 */
@Service
@Singleton
public class MyModel implements MyPublisher{
    @Inject
    MyService myService;

    @Inject
    Topic<MyEvent> myEventTopic;

    // this mehod will be invoked after constructed
    public void postConstruct() {
        System.out.println("Call postConstruct method!");
        System.out.println(this.hashCode());
    }

    public void act() {
        myService.say();
    }

    public void publish() {
        System.out.println("Publish a new event!");
        myEventTopic.publish(new MyEvent("this is my event"));
    }
}
