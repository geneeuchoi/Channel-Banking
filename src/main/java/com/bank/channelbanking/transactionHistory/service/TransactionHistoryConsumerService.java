package com.bank.channelbanking.transactionHistory.service;

import com.bank.channelbanking.transaction.service.TransactionConsumerService;
import com.bank.channelbanking.transactionHistory.dto.event.TransactionEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionHistoryConsumerService {

    private final TransactionHistoryService transactionHistoryService;
    private final TransactionConsumerService transactionConsumerService;

    @KafkaListener(topics = "transaction-history-topic")
    public void consume(TransactionEvent transactionEvent) {
        try {
            log.info("[kafka] 거래 이벤트 수신: " + transactionEvent);
            transactionHistoryService.saveTransactionHistory(transactionEvent);
            transactionConsumerService.changeStatus(transactionEvent);
            log.info("[kafka] 거래 내역 저장 완료: " + transactionEvent);
        } catch (Exception e) {
            log.error("[kafka] 거래 내역 저장 에러 발생: " + e.getMessage());
            e.printStackTrace();
        }
    }

}