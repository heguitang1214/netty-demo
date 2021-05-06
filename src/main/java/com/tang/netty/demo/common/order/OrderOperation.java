package com.tang.netty.demo.common.order;


import com.google.common.util.concurrent.Uninterruptibles;
import com.tang.netty.demo.common.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * 订单操作
 *
 * @author heguitang
 */
@Data
@Slf4j
@EqualsAndHashCode(callSuper = true)
public class OrderOperation extends Operation {

    private int tableId;
    private String dish;

    public OrderOperation(int tableId, String dish) {
        this.tableId = tableId;
        this.dish = dish;
    }

    @Override
    public OrderOperationResult execute() {
        log.info("order's executing startup with orderRequest: " + toString());
        // 模拟业务耗时
        Uninterruptibles.sleepUninterruptibly(3, TimeUnit.SECONDS);
        log.info("order's executing complete");
        return new OrderOperationResult(tableId, dish, true);
    }
}
