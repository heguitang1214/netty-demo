package com.tang.netty.demo.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * 订单数据解码
 * 处理半包和粘包问题，获取的是byteBuffer
 *
 * @author heguitang
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    public OrderFrameDecoder() {
        super(10240, 0, 2, 0, 2);
    }

}
