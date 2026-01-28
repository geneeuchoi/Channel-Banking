package com.bank.channelbanking.transactionHistory.dto.response;

import com.bank.channelbanking.transactionHistory.entity.TransactionHistory;
import lombok.Builder;

@Builder
public record TransactionHistoryResponse(
        Long id,
        String type,
        Long targetUserId,
        Long amount
) {
    public static TransactionHistoryResponse from (TransactionHistory history, Long loginUserId) {
        boolean isSender = history.getSenderUserId().equals(loginUserId);

        return TransactionHistoryResponse.builder()
                .id(history.getId())
                .type(isSender ? "OUT" : "IN")
                .targetUserId(isSender ? history.getReceiverUserId() : history.getSenderUserId())
                .amount(history.getAmount())
                .build();
    }
}
