package com.bank.channelbanking.user.dto.response;

import lombok.Builder;

@Builder
public record SignUpResponse(String email, String name) {
}
