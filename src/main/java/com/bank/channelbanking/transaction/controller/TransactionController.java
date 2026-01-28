package com.bank.channelbanking.transaction.controller;

import com.bank.channelbanking.global.annotation.Idempotent;
import com.bank.channelbanking.security.service.CustomUserDetails;
import com.bank.channelbanking.transaction.dto.request.InternalTransferRequest;
import com.bank.channelbanking.transaction.dto.request.TransferRequest;
import com.bank.channelbanking.transaction.dto.response.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction API", description = "Transaction Controller")
public class TransactionController {

    private final TransactionClient transactionClient;

    //@Idempotent
    @PostMapping("/transfer")
    @Operation(summary = "transfer", description = "송금")
    public TransferResponse transfer(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody TransferRequest transferRequest) {
        InternalTransferRequest internalTransferRequest = InternalTransferRequest.builder()
                .userId(customUserDetails.getUserId())
                .accountNumber(transferRequest.accountNumber())
                .amount(transferRequest.amount())
                .build();

        return transactionClient.requestTransfer(internalTransferRequest);
    }
}
