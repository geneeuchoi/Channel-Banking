package com.bank.channelbanking.account.controller;

import com.bank.channelbanking.account.dto.response.CreateAccountResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-banking", contextId = "accountClient", url = "http://localhost:8081")
public interface AccountClient {

    @PostMapping("/api/v1/account")
    CreateAccountResponse requestCreateAccount(@RequestBody Long userId);

}
