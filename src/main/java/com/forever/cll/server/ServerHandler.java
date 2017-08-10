package com.forever.cll.server;


import com.forever.cll.common.Request;
import com.forever.cll.common.Response;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelStateEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;

/**
 * Created by forever on 2017/8/10.
 */
public class ServerHandler extends SimpleChannelHandler {

    private Map<String,Object> serviceMap;

    public ServerHandler(Map<String, Object> serviceMap) {
        this.serviceMap = serviceMap;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent event) throws Exception {
        Request request = (Request) event.getMessage();
        Response response = new Response();
        //调用请求类的请求方法执行并返回执行结果
        Object invoke = null;
        try {
            Object requestBean = serviceMap.get(request.getClassName());
            Class<?> requestClazz = Class.forName(request.getClassName());
            Method method = requestClazz.getMethod(request.getMethodName(), request.getParameterTypes());
            invoke = method.invoke(requestBean, request.getParameters());
            response.setRequestId(UUID.randomUUID().toString());
            response.setResult(invoke);
        } catch (Exception e) {
            response.setError(e);
            response.setRequestId(UUID.randomUUID().toString());
        }
        System.out.println(request+""+response);
        //返回执行结果
        ctx.getChannel().write(response);

    }

    @Override
    public void channelConnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelConnected(ctx, e);
    }

    @Override
    public void channelDisconnected(ChannelHandlerContext ctx, ChannelStateEvent e) throws Exception {
        super.channelDisconnected(ctx, e);
    }
}
