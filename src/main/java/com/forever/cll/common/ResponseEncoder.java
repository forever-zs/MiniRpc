package com.forever.cll.common;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.oneone.OneToOneEncoder;

/**
 * Created by forever on 2017/8/10.
 */
public class ResponseEncoder  extends OneToOneEncoder{
    @Override
    protected Object encode(ChannelHandlerContext channelHandlerContext, Channel channel, Object o) throws Exception {
        Response Response = (Response) o;
        ChannelBuffer channelBuffer = ChannelBuffers.dynamicBuffer();
        byte[] bytes = SerializationUtil.serialize(Response);
        channelBuffer.writeBytes(bytes);
        return channelBuffer;
    }
}
