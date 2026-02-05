package com.bank.channelbanking.global.exception;

public class PendingTransactionExistsException extends RuntimeException {

    public PendingTransactionExistsException() {
        super("이미 처리 중인 송금 요청이 있습니다. 잠시 후 다시 시도해주세요.");
    }

    public PendingTransactionExistsException(String message) {
        super(message);
    }
}
