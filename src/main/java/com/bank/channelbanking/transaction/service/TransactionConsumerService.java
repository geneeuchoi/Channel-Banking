package com.bank.channelbanking.transaction.service;

import com.bank.channelbanking.global.exception.PendingTransactionNotExistsException;
import com.bank.channelbanking.transaction.entity.Transaction;
import com.bank.channelbanking.transaction.entity.TransactionStatus;
import com.bank.channelbanking.transaction.repository.TransactionRepository;
import com.bank.channelbanking.transactionHistory.dto.event.TransactionEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionConsumerService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public void changeStatus(TransactionEvent transactionEvent) {
        Transaction pendingTransactions =
                transactionRepository.findByIdAndStatus(transactionEvent.getTransactionId(), TransactionStatus.PENDING)
                        .orElseThrow(() -> new PendingTransactionNotExistsException());

        pendingTransactions.updateStatus(TransactionStatus.COMPLETED);
    }
}
