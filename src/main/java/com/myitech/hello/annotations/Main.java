package com.myitech.hello.annotations;

import javax.inject.Inject;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by A.T on 2018/1/24.
 */
public class Main {
    public static void main( String[] args ) {
        System.out.println( "Hello World!" );

        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        try {
            Class clazz = loader.loadClass("com.myitech.annotation.Hello");

            Constructor constructor = clazz.getDeclaredConstructor(new Class[] {String.class});
            Hello hello = (Hello) constructor.newInstance("hello");
            assert hello.getClass().getAnnotation(MyAnnotation.class).value().equals("MyGod");

            for (Annotation annotation : hello.getClass().getAnnotations()) {
                System.out.println(annotation.toString());
            }

            Method[] methods = hello.getClass().getDeclaredMethods();

            for (Method m : methods) {
                MyMethod method = m.getAnnotation(MyMethod.class);

                if (method != null) {
                    System.out.println("Annotation Method: " + m.getName());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
