package com.forever.cll.client;


import com.forever.cll.common.Request;
import com.forever.cll.common.Response;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 * Created by forever on 2017/8/9.
 */
public class RPCProxy {

    private String address;
    private int port;

    public RPCProxy(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public <T>T proxy(Class<?> clazz){
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] { clazz }, new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Request request = new Request();
                request.setClassName(method.getDeclaringClass().getName());
                request.setMethodName(method.getName());
                request.setParameters(args);
                request.setRequestId(UUID.randomUUID().toString());
                request.setParameterTypes(method.getParameterTypes());
                RpcClient client =new RpcClient(address,port);
                Response response = client.send(request);
                if (response.getError()!=null){
                    throw response.getError();
                }
                else{
                    return response;
                }
            }
        });
    }

}
