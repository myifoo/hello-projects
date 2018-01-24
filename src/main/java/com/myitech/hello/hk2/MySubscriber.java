package com.myitech.hello.hk2;

import org.glassfish.hk2.api.messaging.MessageReceiver;
import org.glassfish.hk2.api.messaging.SubscribeTo;
import org.jvnet.hk2.annotations.Service;

import javax.inject.Singleton;

/**
 * Created by A.T on 2018/1/23.
 */
@Service
@Singleton
@MessageReceiver
public class MySubscriber {
    public void actOnEvent(@SubscribeTo MyEvent event) {
        System.out.println("Receiver the event: ");
        event.act();
    }
}
