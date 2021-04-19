package com.tang.netty.demo.server.codec;

import com.tang.netty.demo.common.RequestMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

/**
 * 订单数据二次解码器
 *
 * @author heguitang
 */
public class OrderProtocolDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage();
        // 把 byteBuf 解码
        requestMessage.decode(byteBuf);
        
        // 数据传递出去
        out.add(requestMessage);
    }
}
