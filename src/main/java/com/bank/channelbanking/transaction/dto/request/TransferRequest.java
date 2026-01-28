package com.bank.channelbanking.transaction.dto.request;

public record TransferRequest(
        String accountNumber,
        long amount
) {
}
