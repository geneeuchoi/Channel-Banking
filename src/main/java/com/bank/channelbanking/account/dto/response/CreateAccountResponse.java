package com.bank.channelbanking.account.dto.response;


public record CreateAccountResponse(
        Long id,
        Long userId,
        String accountNumber,
        Long balance
) {
}
