package com.tang.netty.demo.common;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 操作响应
 *
 * @author heguitang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class OperationResult extends MessageBody {

}
