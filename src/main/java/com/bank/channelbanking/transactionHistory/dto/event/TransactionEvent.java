package com.bank.channelbanking.transactionHistory.dto.event;

import jakarta.persistence.Column;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private Long transactionId;
    private Long senderUserId;
    private Long receiverUserId;
    private Long amount;
    private String type;
    private LocalDateTime transactedAt;
}