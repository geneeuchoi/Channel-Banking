package com.bank.channelbanking.transactionHistory.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TransactionHistoryPageResponse(
        List<TransactionHistoryResponse> transactionHistoryResponses,
        Long lastId,
        boolean hasNext) {
}
