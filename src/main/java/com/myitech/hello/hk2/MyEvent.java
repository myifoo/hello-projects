package com.myitech.hello.hk2;

/**
 * Created by A.T on 2018/1/23.
 */
public class MyEvent {
    String message;

    public MyEvent(String message) {
        this.message = message;
    }

    public void act() {
        System.out.println(message);
    }

}
