package com.tang.netty.demo.server.codec;


import io.netty.handler.codec.LengthFieldPrepender;

public class OrderFrameEncoder extends LengthFieldPrepender {

    /**
     * LengthFieldPrepender：预设的length长度是2
     */
    public OrderFrameEncoder() {
        super(2);
    }
}
