package com.bank.channelbanking.transaction.controller;

import com.bank.channelbanking.transaction.dto.request.InternalTransferRequest;
import com.bank.channelbanking.transaction.dto.response.TransferResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "core-banking", contextId = "transactionClient", url = "http://localhost:8081")
public interface TransactionClient {

    @PostMapping("/api/v1/transaction/transfer")
    TransferResponse requestTransfer(@RequestBody InternalTransferRequest internalTransferRequest);
}
