package com.forever.cll.server;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Created by forever on 2017/8/10.
 */
public class RpcStarter {
    public static void startServer(String path){
        new ClassPathXmlApplicationContext(path);
    }
}
