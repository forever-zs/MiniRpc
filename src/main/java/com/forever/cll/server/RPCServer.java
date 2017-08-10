package com.forever.cll.server;

import com.forever.cll.common.RequsetDecoder;
import com.forever.cll.common.ResponseEncoder;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by forever on 2017/8/9.
 */
public class RPCServer implements ApplicationContextAware {
    private int port;
    public RPCServer(int port) {
        this.port = port;
    }
    private Map<String,Object> serviceMap = new HashMap<String,Object>();

    public void startServer(){
        ServerBootstrap bootstrap = new ServerBootstrap();
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService work = Executors.newCachedThreadPool();
        bootstrap.setFactory(new NioServerSocketChannelFactory(boss,work));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("encoder",new ResponseEncoder());
                pipeline.addLast("decoder",new RequsetDecoder());
                pipeline.addLast("handler",new ServerHandler(serviceMap));
                return pipeline;
            }
        });
        bootstrap.bind(new InetSocketAddress(port));
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(RPCService.class);
        for(Map.Entry<String,Object> entry :beansWithAnnotation.entrySet()){
            String interfaceName = entry.getValue().getClass()
                    .getAnnotation(RPCService.class).value().getName();
            serviceMap.put(interfaceName,entry.getValue());
        }
        startServer();
    }
}
