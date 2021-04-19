package com.tang.netty.demo.common;

import lombok.Data;

/**
 * 消息头
 */
@Data
public class MessageHeader {

    private int version = 1;

    private int opCode;

    private long streamId;

}
