package com.tang.netty.demo.client.codec;

import com.tang.netty.demo.common.Operation;
import com.tang.netty.demo.common.RequestMessage;
import com.tang.netty.demo.util.IdUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class OperationToRequestMessageEncoder extends MessageToMessageEncoder<Operation> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Operation operation, List<Object> out) throws Exception {
        RequestMessage requestMessage = new RequestMessage(IdUtil.nextId(), operation);

        out.add(requestMessage);
    }
}
