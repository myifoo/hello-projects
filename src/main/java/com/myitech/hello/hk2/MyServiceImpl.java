package com.myitech.hello.hk2;

import org.jvnet.hk2.annotations.Service;

/**
 * Created by A.T on 2018/1/23.
 */
@Service
public class MyServiceImpl implements MyService {

    public void say() {
        System.out.println("Hello HK2!");
    }
}
