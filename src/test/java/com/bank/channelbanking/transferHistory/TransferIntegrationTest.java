package com.bank.channelbanking.transferHistory;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@SpringBootTest
public class TransferIntegrationTest {

    @Test
    void 읽기_테스트() throws InterruptedException  {

        ExecutorService executorService = Executors.newFixedThreadPool(8);
        CountDownLatch latch = new CountDownLatch(1);

        executorService.execute(()-> {
            transferHistoryClient.requestGetTransferHistory(1L);
            latch.countDown();
        });

    }
}
