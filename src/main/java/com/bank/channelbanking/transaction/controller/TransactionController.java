package com.bank.channelbanking.transaction.controller;

import com.bank.channelbanking.security.service.CustomUserDetails;
import com.bank.channelbanking.transaction.dto.request.InternalTransferRequest;
import com.bank.channelbanking.transaction.dto.request.TransferRequest;
import com.bank.channelbanking.transaction.dto.response.TransferResponse;
import com.bank.channelbanking.transaction.entity.Transaction;
import com.bank.channelbanking.transaction.service.TransactionService;
import com.bank.channelbanking.transaction.entity.TransactionStatus;
import com.bank.channelbanking.transactionHistory.service.TransactionHistoryService;
import io.github.resilience4j.retry.annotation.Retry;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/transaction")
@Tag(name = "Transaction API", description = "Transaction Controller")
public class TransactionController {

    private final TransactionClient transactionClient;
    private final TransactionHistoryService transactionHistoryService;
    private final TransactionService transactionService;

    //@Idempotent
    @PostMapping("/transfer")
    @Operation(summary = "transfer", description = "송금")
    public TransferResponse transfer(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                     @RequestBody TransferRequest transferRequest) {
        Long userId = customUserDetails.getUserId();

        Transaction pendingTransaction = transactionService.checkAndCreatePendingTransaction(
                userId,
                transferRequest.amount()
        );

        try {
            return transferWithRetry(userId, transferRequest, pendingTransaction.getId());
        } catch (Exception e) {
            log.error("Transfer failed for user {}, transaction {}: {}",
                     userId, pendingTransaction.getId(), e.getMessage());
            transactionService.updateTransactionStatus(pendingTransaction.getId(), TransactionStatus.FAILED);
            throw e;
        }
    }

    @Retry(name = "coreBankingTransfer", fallbackMethod = "transferFallback")
    private TransferResponse transferWithRetry(Long userId, TransferRequest transferRequest, Long transactionId) {
        InternalTransferRequest internalTransferRequest = InternalTransferRequest.builder()
                .transactionId(transactionId)
                .userId(userId)
                .accountNumber(transferRequest.accountNumber())
                .amount(transferRequest.amount())
                .build();

        log.debug("Attempting transfer to core banking: userId={}, transactionId={}", userId, transactionId);
        return transactionClient.requestTransfer(internalTransferRequest);
    }
}
