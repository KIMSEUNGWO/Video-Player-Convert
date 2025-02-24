package com.video.jours.component;

import com.rabbitmq.client.Channel;
import com.video.jours.dto.UploadStatus;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

import static com.video.jours.enums.PathType.VIDEO;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExecutorRecovery {

    private final Map<String, UploadStatus> activeUploads = new ConcurrentHashMap<>();
    private final ExecutorService executor;
    private final DirectoryManager directoryManager;
    private final PathManager pathManager;


    public void execute(UploadStatus status, Runnable runnable) throws RejectedExecutionException {

        String key = status.message().getKey();
        activeUploads.put(key, status);

        executor.submit(() -> {
            try {
                runnable.run();
            } finally {
                activeUploads.remove(key);
            }
        });
    }

    @PreDestroy
    @EventListener(ContextClosedEvent.class)
    public void shutdown() {
        // 현재 진행 중인 작업들의 상태 저장
        activeUploads.forEach((key, status) -> {
            try(Channel channel = status.channel()) {
                channel.basicNack(status.tag(), false, true);
                directoryManager.deleteIfExists(pathManager.get(VIDEO, key));
            } catch (Exception e) {
                log.error("Failed to handle shutdown for upload: {}", key, e);
            }
        });
    }
}
