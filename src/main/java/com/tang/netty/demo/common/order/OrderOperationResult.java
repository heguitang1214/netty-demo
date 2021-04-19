package com.tang.netty.demo.common.order;

import com.tang.netty.demo.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 订单操作返回结果
 *
 * @author heguitang
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderOperationResult extends OperationResult {

    private final int tableId;
    private final String dish;
    private final boolean complete;

}
