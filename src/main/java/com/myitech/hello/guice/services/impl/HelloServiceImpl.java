package com.myitech.hello.guice.services.impl;

import com.myitech.hello.guice.services.HelloService;

/**
 * Created by A.T on 2018/1/24.
 */
public class HelloServiceImpl implements HelloService {

    public void say() {
        System.out.println("Hello Guice!");
    }
}
