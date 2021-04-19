package com.tang.netty.demo.common;

/**
 * 响应消息
 *
 * @author heguitang
 */
public class ResponseMessage extends Message<OperationResult> {

    @Override
    public Class getMessageBodyDecodeClass(int opcode) {
        return OperationType.fromOpCode(opcode).getOperationResultClazz();
    }
    
}
