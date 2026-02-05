package com.bank.channelbanking.transaction.dto.request;

import lombok.Builder;

@Builder
public record InternalTransferRequest(
        Long transactionId,
        Long userId,
        String accountNumber,
        Long amount
) {
}
