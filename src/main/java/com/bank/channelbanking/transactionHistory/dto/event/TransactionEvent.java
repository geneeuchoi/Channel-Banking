package com.bank.channelbanking.transactionHistory.dto.event;

import jakarta.persistence.Column;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private Long senderUserId;
    private Long receiverUserId;
    private Long amount;
    private Long senderBalanceAfter;
    private Long receiverBalanceAfter;
    private String type;
}