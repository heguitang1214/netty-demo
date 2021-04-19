package com.tang.netty.demo.common;

/**
 * 操作
 *
 * @author heguitang
 */
public abstract class Operation extends MessageBody {

    public abstract OperationResult execute();

}
