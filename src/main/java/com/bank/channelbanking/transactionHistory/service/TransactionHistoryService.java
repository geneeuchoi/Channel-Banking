package com.bank.channelbanking.transactionHistory.service;

import com.bank.channelbanking.transactionHistory.dto.event.TransactionEvent;
import com.bank.channelbanking.transactionHistory.dto.response.TransactionHistoryPageResponse;
import com.bank.channelbanking.transactionHistory.dto.response.TransactionHistoryResponse;
import com.bank.channelbanking.transactionHistory.entity.TransactionHistory;
import com.bank.channelbanking.transactionHistory.repository.TransactionHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryService {

    private final TransactionHistoryRepository transactionHistoryRepository;

    @Transactional(readOnly = true)
    public TransactionHistoryPageResponse requestRead(Long userId, Long lastId, int limit) {
        List<TransactionHistory> histories = transactionHistoryRepository.findTransactionHistories(userId, lastId, limit+1);
        List<TransactionHistoryResponse>  transactionHistoryResponses= histories.stream()
                .map(history -> TransactionHistoryResponse.from(history, userId))
                .collect(Collectors.toList());


        return TransactionHistoryPageResponse.builder()
                .transactionHistoryResponses(transactionHistoryResponses)
                .lastId(findNextId(histories))
                .hasNext(hasNext(histories, limit))
                .build();
    }

    private boolean hasNext(List<TransactionHistory> histories, int limit) {
        boolean hasNext = false;
        if (histories.size() > limit) {
            hasNext = true;
            histories.remove(limit);
        }
        return hasNext;
    }

    private Long findNextId(List<TransactionHistory> histories) {
        Long nextLastId = null;
        if (!histories.isEmpty()) {
            nextLastId = histories.get(histories.size() - 1).getId();
        }
        return nextLastId;
    }


    @Transactional
    public void saveTransactionHistory(TransactionEvent event) {
        TransactionHistory history = TransactionHistory.builder()
                .senderUserId(event.getSenderUserId())
                .receiverUserId(event.getReceiverUserId())
                .amount(event.getAmount())
                .type(event.getType())
                .build();

        transactionHistoryRepository.save(history);
    }
}
