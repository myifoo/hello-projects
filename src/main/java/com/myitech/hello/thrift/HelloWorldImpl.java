package com.myitech.hello.thrift;

import org.apache.thrift.TException;

/**
 * Created by A.T on 2018/1/24.
 */
public class HelloWorldImpl implements HelloWorldService.Iface {

    public HelloWorldImpl() {
    }

    public String sayHello(String username) throws TException {
        return "Hi," + username + " welcome to my blog www.micmiu.com";
    }

}
