package com.bank.channelbanking.security.dto.request;

public record LoginRequest(
        String username,
        String password
) {
}
