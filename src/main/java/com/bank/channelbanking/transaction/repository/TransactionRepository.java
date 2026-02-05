package com.bank.channelbanking.transaction.repository;

import com.bank.channelbanking.transaction.entity.Transaction;
import com.bank.channelbanking.transaction.entity.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findBySenderIdAndStatus(Long senderId, TransactionStatus status);

    Optional<Transaction> findByIdAndStatus(Long id, TransactionStatus status);
}
