package com.bank.channelbanking.global.exception;

public class PendingTransactionNotExistsException extends RuntimeException {

    public PendingTransactionNotExistsException() {
        super("거래 중인 송금 요청이 아닙니다.");
    }

    public PendingTransactionNotExistsException(String message) {
        super(message);
    }
}
