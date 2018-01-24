package com.myitech.hello.annotations;

import javax.annotation.Resource;
import javax.inject.Inject;

/**
 * Created by A.T on 2018/1/24.
 */
@MyAnnotation("MyGod")
@Resource
public class Hello {

    private String name;

    @Inject
    public Hello(String name) {
        this.name = name;
    }

    @MyMethod
    public void says() {
        System.out.println("Hello Annotation!");
    }
}

