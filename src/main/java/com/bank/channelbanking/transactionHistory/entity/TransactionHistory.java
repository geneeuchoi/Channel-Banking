package com.bank.channelbanking.transactionHistory.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "transaction_histories")
@NoArgsConstructor
@AllArgsConstructor
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

    @Column(name = "sender_balance_after")
    private Long senderBalanceAfter;

    @Column(name = "receiver_balance_after")
    private Long receiverBalanceAfter;

    private String type;

    @Builder
    public TransactionHistory(Long senderUserId, Long receiverUserId, Long amount, Long senderBalanceAfter, Long receiverBalanceAfter, String type) {
        this.senderUserId = senderUserId;
        this.receiverUserId = receiverUserId;
        this.amount = amount;
        this.senderBalanceAfter = senderBalanceAfter;
        this.receiverBalanceAfter = receiverBalanceAfter;
        this.type = type;
    }
}