package com.bank.channelbanking.transactionHistory.service;

import com.bank.channelbanking.transactionHistory.dto.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionHistoryConsumerService {

    private final TransactionHistoryService transactionHistoryService;

    @KafkaListener(topics = "transaction-history-topic")
    public void consume(TransactionEvent transactionEvent) {
        try {
            System.out.println("<<< [채널계] 거래 이벤트 수신: " + transactionEvent);
            transactionHistoryService.saveTransactionHistory(transactionEvent);
            System.out.println("<<< [채널계] 거래 내역 저장 완료: ");
        } catch (Exception e) {
            System.err.println("!!! 거래 내역 저장 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }
}