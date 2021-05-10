package com.tang.netty.demo.server.handler;

import com.tang.netty.demo.common.Operation;
import com.tang.netty.demo.common.OperationResult;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.common.ResponseMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * 业务处理
 * 对 Inbound 事件的处理
 * SimpleChannelInboundHandler：自动释放byteBuffer
 *
 * @author heguitang
 */
@Slf4j
public class OrderServerProcessHandler extends SimpleChannelInboundHandler<RequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage requestMessage) throws Exception {
        // 模拟内存泄漏问题
//        ByteBuf buffer = ctx.alloc().buffer();

        // SimpleChannelInboundHandler 也会释放 requestMessage，只不过会释放不了，因为：
        // RequestMessage 本身并不是一个ReferenceCounted， 在SimpleChannelInboundHandler 对它release应该是没有效果的
        // 主要的好处是：不用管这些细致末节了，直接release，需要release的会释放，不需要的（没有实现ReferenceCounted）不释放。所以对我们来说省心友好。
        Operation operation = requestMessage.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        // ctx本身就是于一个channel相关的一切。
        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(responseMessage);
        } else {
            // 数据丢掉；数据存起来   避免OOM
            log.error("not writable now, message dropped");
        }
    }


}
