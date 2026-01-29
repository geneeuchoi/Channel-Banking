package com.bank.channelbanking.transactionHistory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@Table(name = "transaction_histories", indexes = {
    @Index(name = "idx_sender_user_id_transaction_history_id",
           columnList = "sender_user_id, transaction_history_id"),
    @Index(name = "idx_receiver_user_id_transaction_history_id",
           columnList = "receiver_user_id, transaction_history_id")
})
public class TransactionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_history_id")
    private Long id;

    @Column(name = "sender_user_id")
    private Long senderUserId;

    @Column(name = "receiver_user_id")
    private Long receiverUserId;

    private Long amount;

    private String type;

    @Column(name = "transacted_at")
    private LocalDateTime transactedAt;

    @Builder
    public TransactionHistory(Long senderUserId, Long receiverUserId, Long amount, String type, LocalDateTime transactedAt) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.type = type;
        this.transactedAt = transactedAt;
    }
}