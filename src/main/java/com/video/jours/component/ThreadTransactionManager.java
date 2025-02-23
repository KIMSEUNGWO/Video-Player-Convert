package com.video.jours.component;

import com.video.jours.entity.VideoStatus;
import com.video.jours.enums.ProcessingStatus;
import com.video.jours.repository.StatusJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;

@Component
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class ThreadTransactionManager {

    private final StatusJpaRepository statusJpaRepository;

    public void updateStatus(String key, ProcessingStatus processingStatus) {
        statusJpaRepository.findById(key)
            .ifPresent(entity -> entity.updateStatus(processingStatus));
    }

    public void updateStatus(VideoStatus status, Consumer<VideoStatus> consumer) {
        statusJpaRepository.findById(status.getStatusKey())
            .ifPresent(consumer);
    }
}
