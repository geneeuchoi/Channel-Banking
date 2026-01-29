package com.bank.channelbanking.transactionHistory.repository;

import com.bank.channelbanking.transactionHistory.entity.TransactionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionHistoryRepository extends JpaRepository<TransactionHistory, Long> {

    @Query(value = "SELECT * FROM transaction_histories " +
            "WHERE (sender_user_id = :userId OR receiver_user_id = :userId) " +
            "AND (:lastId IS NULL OR transaction_history_id < :lastId) " +
            "ORDER BY transaction_history_id DESC LIMIT :limit", nativeQuery = true)
    List<TransactionHistory> findTransactionHistories(@Param("userId") Long userId,
                                          @Param("lastId") Long lastId,
                                          @Param("limit") int limit);
}
