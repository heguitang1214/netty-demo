package com.tang.netty.demo.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;


/**
 * 订单数据解码
 * 处理半包和粘包问题，获取的是byteBuffer
 *
 * @author heguitang
 */
public class OrderFrameDecoder extends LengthFieldBasedFrameDecoder {

    /**
     * maxFrameLength：最大的长度是 10240
     * lengthFieldOffset：长度字段的位移，从0开始
     * lengthFieldLength：长度字段的长度是多少？预设为2
     * lengthAdjustment：需不需要调整length
     * initialBytesToStrip：是不是要把头字段去掉？从2开始，也就是去掉
     */
    public OrderFrameDecoder() {
        super(10240, 0, 2, 0, 2);
    }

}
