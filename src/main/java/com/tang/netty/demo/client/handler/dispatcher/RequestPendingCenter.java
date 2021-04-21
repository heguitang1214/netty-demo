package com.tang.netty.demo.client.handler.dispatcher;


import com.tang.netty.demo.common.OperationResult;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Stream和Future的映射关系
 *
 * @author heguitang
 */
public class RequestPendingCenter {

    private Map<Long, OperationResultFuture> map = new ConcurrentHashMap<>();

    /**
     * 请求使用
     *
     * @param streamId 请求id
     * @param future   请求future
     */
    public void add(Long streamId, OperationResultFuture future) {
        this.map.put(streamId, future);
    }

    /**
     * 响应使用
     *
     * @param streamId        请求id
     * @param operationResult 请求响应
     */
    public void set(Long streamId, OperationResult operationResult) {
        OperationResultFuture operationResultFuture = this.map.get(streamId);
        if (operationResultFuture != null) {
            operationResultFuture.setSuccess(operationResult);
            this.map.remove(streamId);
        }
    }


}
