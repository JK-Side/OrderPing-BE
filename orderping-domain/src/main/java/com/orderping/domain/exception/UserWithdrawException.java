package com.orderping.domain.exception;

public class UserWithdrawException extends RuntimeException {
    public UserWithdrawException(String step, Throwable cause) {
        super("회원 탈퇴 실패 - " + step, cause);
    }
}
