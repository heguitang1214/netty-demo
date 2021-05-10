package com.tang.netty.demo.client.handler;

import io.netty.handler.timeout.IdleStateHandler;

/**
 * 客户端保活
 */
public class ClientIdleCheckHandler extends IdleStateHandler {

    public ClientIdleCheckHandler() {
        // Idle 发生，会触发 event
        super(0, 5, 0);
    }

}
