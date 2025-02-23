package com.video.jours.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class ExecutorConfig {

    @Bean
    public BlockingQueue<Runnable> videoQueue() {
        return new LinkedBlockingQueue<>();
    }

    @Bean
    public ThreadPoolExecutor videoExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            2, // 코어 스레드 수
            2, // 최대 스레드 수
            0L,
            TimeUnit.MILLISECONDS,
            videoQueue(),
            new ThreadPoolExecutor.CallerRunsPolicy() // 거부된 작업 처리 정책 추가
        );

        // 예외 모니터링 추가
        executor.setThreadFactory(r -> {
            Thread t = new Thread(r);
            t.setUncaughtExceptionHandler((thread, e) -> {
                log.error("Thread {} threw exception: {}", thread.getName(), e.getMessage(), e);
            });
            return t;
        });

        return executor;
    }
}
