package com.forever.cll.client;

import com.forever.cll.common.Request;
import com.forever.cll.common.RequestEncoder;
import com.forever.cll.common.Response;
import com.forever.cll.common.ResponseDecoder;
import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.*;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by forever on 2017/8/9.
 */
public class RpcClient extends SimpleChannelHandler{

    private Response response;
    private Object obj = new Object();
    private String address;
    private int port;

    public RpcClient(String address, int port) {
        this.address = address;
        this.port = port;
    }

    public Response send(Request request) throws InterruptedException {
        ClientBootstrap bootstrap = new ClientBootstrap();
        ExecutorService boss = Executors.newCachedThreadPool();
        ExecutorService work = Executors.newCachedThreadPool();

        bootstrap.setFactory(new NioClientSocketChannelFactory(boss,work));
        bootstrap.setPipelineFactory(new ChannelPipelineFactory() {
            @Override
            public ChannelPipeline getPipeline() throws Exception {
                ChannelPipeline pipeline = Channels.pipeline();
                pipeline.addLast("decoder",new ResponseDecoder());
                pipeline.addLast("encoder",new RequestEncoder());
                pipeline.addLast("handler",RpcClient.this);
                return pipeline;
            }
        });
        ChannelFuture connect = bootstrap.connect(new InetSocketAddress(address, port)).sync();
        connect.getChannel().write(request).sync();
        synchronized (obj){
            obj.wait();
        }
        connect.getChannel().close().sync();

        return this.response;
    }

    @Override
    public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
        response  = (Response) e.getMessage();
        System.out.println(response);
        synchronized (obj){
            obj.notify();
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
        ctx.getChannel().close();
    }
}
