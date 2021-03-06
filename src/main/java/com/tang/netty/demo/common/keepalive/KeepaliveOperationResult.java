package com.tang.netty.demo.common.keepalive;

import com.tang.netty.demo.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Keepalive ζδ½εεΊ
 *
 * @author heguitang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KeepaliveOperationResult extends OperationResult {

    private final long time;

}
