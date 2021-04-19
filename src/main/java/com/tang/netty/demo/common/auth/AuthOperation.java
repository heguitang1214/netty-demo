package com.tang.netty.demo.common.auth;


import com.tang.netty.demo.common.Operation;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.java.Log;


/**
 * 认证操作
 *
 * @author heguitang
 */
@Data
@Log
@EqualsAndHashCode(callSuper = true)
public class AuthOperation extends Operation {

    private final String userName;
    private final String password;

    @Override
    public AuthOperationResult execute() {
        if ("admin".equalsIgnoreCase(this.userName)) {
            AuthOperationResult orderResponse = new AuthOperationResult(true);
            return orderResponse;
        }

        return new AuthOperationResult(false);
    }
}
