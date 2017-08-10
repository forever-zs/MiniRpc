package com.forever.cll.common;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * Created by forever on 2017/8/10.
 */
public class ResponseDecoder extends FrameDecoder{


    @Override
    protected Object decode(ChannelHandlerContext channelHandlerContext, Channel channel, ChannelBuffer channelBuffer) throws Exception {
        byte[] bytes = new byte[channelBuffer.readableBytes()];
        channelBuffer.readBytes(bytes);
        Response Response = SerializationUtil.deserialize(bytes, Response.class);
        return Response;
    }

}
