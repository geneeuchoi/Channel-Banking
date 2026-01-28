package com.bank.channelbanking.transactionHistory.controller;

import com.bank.channelbanking.global.annotation.Idempotent;
import com.bank.channelbanking.security.service.CustomUserDetails;
import com.bank.channelbanking.transactionHistory.dto.response.TransactionHistoryResponse;
import com.bank.channelbanking.transactionHistory.service.TransactionHistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction_history")
@Tag(name = "Transaction History API", description = "Transaction History Controller")
public class TransactionHistoryController {

    private final TransactionHistoryService transactionHistoryService;

    //@Idempotent
    @GetMapping("/transfer")
    @Operation(summary = "read transfer history", description = "거래내역 조회")
    public List<TransactionHistoryResponse> read(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return transactionHistoryService.requestRead(customUserDetails.getUserId());
    }

}
