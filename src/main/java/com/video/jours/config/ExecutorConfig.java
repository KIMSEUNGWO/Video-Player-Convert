package com.video.jours.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ExecutorConfig {

    private final PropertyConfig propertyConfig;

//    @Bean
//    public BlockingQueue<Runnable> videoQueue() {
//        return new LinkedBlockingQueue<>();
//    }

    @Bean
    BlockingQueue<Runnable> videoQueue() {
        return new ArrayBlockingQueue<>(propertyConfig.getMaxQueueSize());
    }

    @Bean
    public ThreadPoolExecutor videoExecutor() {
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
            propertyConfig.getThreadCoreCount(), // 코어 스레드 수
            propertyConfig.getThreadMaxCount(), // 최대 스레드 수
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
