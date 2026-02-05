package com.bank.channelbanking.transaction.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@Table(name="transactions")
@NoArgsConstructor
public class Transaction {

    @Column(name="transaction_id")
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private long senderId;

    private String accountNumber;

    private long amount;

    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;

    @Column(name = "transacted_at")
    private LocalDateTime transactedAt;

    @Builder
    public Transaction(long senderId, String accountNumber, long amount, TransactionStatus status, LocalDateTime transactedAt) {
        this.senderId = senderId;
        this.accountNumber = accountNumber;
        this.amount = amount;
        this.status = status;
        this.transactedAt = transactedAt;
    }

    public void updateStatus(TransactionStatus status) {
        this.status = status;
    }
}
