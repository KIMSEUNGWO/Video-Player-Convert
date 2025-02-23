package com.video.jours.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableScheduling
public class ExecutorMonitor {

    private final ThreadPoolExecutor videoExecutor;
    private final BlockingQueue<Runnable> videoQueue;

    @Scheduled(fixedDelay = 5000)
    public void monitorVideo() {
        // 현재 처리중인 스레드 수와 큐에 남은 처리 수 확인
        log.info("최대 허용 스레드 수 : {}, 처리중인 스레드 수 : {}, 대기중인 스레드 수 : {}",
            videoExecutor.getMaximumPoolSize(), videoExecutor.getActiveCount(), videoQueue.size());
    }

}
