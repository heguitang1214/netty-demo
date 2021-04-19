package com.tang.netty.demo.common.auth;

import com.tang.netty.demo.common.OperationResult;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 认证操作返回结果
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthOperationResult extends OperationResult {

    private final boolean passAuth;

}
