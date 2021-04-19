package com.tang.netty.demo.server.handler;

import com.tang.netty.demo.common.Operation;
import com.tang.netty.demo.common.OperationResult;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.common.ResponseMessage;
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
        Operation operation = requestMessage.getMessageBody();
        OperationResult operationResult = operation.execute();

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessageHeader(requestMessage.getMessageHeader());
        responseMessage.setMessageBody(operationResult);

        if (ctx.channel().isActive() && ctx.channel().isWritable()) {
            ctx.writeAndFlush(responseMessage);
        } else {
            log.error("not writable now, message dropped");
        }
    }


}
