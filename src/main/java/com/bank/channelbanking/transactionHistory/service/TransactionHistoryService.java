package com.bank.channelbanking.transactionHistory.service;

import com.bank.channelbanking.transactionHistory.dto.event.TransactionEvent;
import com.bank.channelbanking.transactionHistory.dto.response.TransactionHistoryResponse;
import com.bank.channelbanking.transactionHistory.entity.TransactionHistory;
import com.bank.channelbanking.transactionHistory.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional(readOnly = true)
    public List<TransactionHistoryResponse> requestRead(long userId) {
        List<TransactionHistory> histories = transactionHistoryRepository.findAllBySenderUserIdOrReceiverUserId(userId, userId);
        return histories.stream()
                .map(history -> TransactionHistoryResponse.from(history, userId))
                .collect(Collectors.toList());    }

    @Transactional
    public void saveTransactionHistory(TransactionEvent event) {
        TransactionHistory history = TransactionHistory.builder()
                .senderUserId(event.getSenderUserId())
                .receiverUserId(event.getReceiverUserId())
                .amount(event.getAmount())
                .senderBalanceAfter(event.getSenderBalanceAfter())
                .receiverBalanceAfter(event.getReceiverBalanceAfter())
                .type(event.getType())
                .build();

        transactionHistoryRepository.save(history);
    }
}
