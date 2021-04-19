package com.tang.netty.demo.common.keepalive;


import com.tang.netty.demo.common.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;

/**
 * Keepalive 操作
 *
 * @author heguitang
 */
@Data
@Log
@EqualsAndHashCode(callSuper = true)
public class KeepaliveOperation extends Operation {

    private long time;

    public KeepaliveOperation() {
        this.time = System.nanoTime();
    }

    @Override
    public KeepaliveOperationResult execute() {
        KeepaliveOperationResult orderResponse = new KeepaliveOperationResult(time);
        return orderResponse;
    }
}
