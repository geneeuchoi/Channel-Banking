package com.bank.channelbanking.account.controller;

import com.bank.channelbanking.account.dto.response.CreateAccountResponse;
import com.bank.channelbanking.global.annotation.Idempotent;
import com.bank.channelbanking.security.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/account")
@Tag(name = "Account API", description = "Account Controller")
public class AccountController {

    private final AccountClient accountClient;

    //@Idempotent
    @PostMapping("")
    @Operation(summary = "open new account", description = "새로운 계좌 개설")
    public CreateAccountResponse create(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return accountClient.requestCreateAccount(customUserDetails.getUserId());
    }


}
