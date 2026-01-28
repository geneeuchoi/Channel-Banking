package com.bank.channelbanking.transaction.dto.response;

public record TransferResponse(
        Long senderBalance,
        Long receiverBalance
) {
}
