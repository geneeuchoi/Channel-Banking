package com.bank.channelbanking.transactionHistory.repository;

import com.bank.channelbanking.transactionHistory.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {
    List<TransactionHistory> findAllBySenderUserIdOrReceiverUserId(long userId, long userId1);

}
