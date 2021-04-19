package com.tang.netty.demo.common.keepalive;

import com.tang.netty.demo.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Keepalive 操作响应
 *
 * @author heguitang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
