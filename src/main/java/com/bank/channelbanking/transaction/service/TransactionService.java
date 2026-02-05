package com.bank.channelbanking.transaction.service;

import com.bank.channelbanking.global.exception.PendingTransactionExistsException;
import com.bank.channelbanking.transaction.entity.Transaction;
import com.bank.channelbanking.transaction.entity.TransactionStatus;
import com.bank.channelbanking.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    @Transactional
    public Transaction checkAndCreatePendingTransaction(Long userId, Long amount) {
        List<Transaction> pendingTransactions =
                transactionRepository.findBySenderIdAndStatus(userId, TransactionStatus.PENDING);

        if (!pendingTransactions.isEmpty()) {
            log.warn("User {} has {} pending transaction(s)", userId, pendingTransactions.size());
            throw new PendingTransactionExistsException();
        }

        try {
            Transaction pendingTransaction = Transaction.builder()
                    .senderId(userId)
                    .amount(amount)
                    .status(TransactionStatus.PENDING)
                    .transactedAt(LocalDateTime.now())
                    .build();

            Transaction saved = transactionRepository.save(pendingTransaction);
            log.info("Created PENDING transaction: id={}, senderId={}, amount={}", saved.getId(), userId, amount);

            return saved;
        } catch (DataIntegrityViolationException e) {
            log.warn("Concurrent transaction detected for user {}: {}", userId, e.getMessage());
            throw new PendingTransactionExistsException();
        }
    }

    @Transactional
    public void updateTransactionStatus(Long transactionId, TransactionStatus status) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new IllegalArgumentException("Transaction not found: " + transactionId));

        transaction.updateStatus(status);
        log.info("Updated transaction status: id={}, status={}", transactionId, status);
    }



}
