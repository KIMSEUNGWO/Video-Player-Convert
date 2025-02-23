package com.video.jours.service;

import com.rabbitmq.client.Channel;
import com.video.jours.component.ExecutorRecovery;
import com.video.jours.dto.UploadStatus;
import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.repository.StatusRepository;
import com.video.jours.repository.UploadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {

    private final UploadRepository uploadRepository;
    private final StatusRepository statusRepository;

    private final ExecutorRecovery executorRecovery;


    public void upload(ConvertRequest message, Channel channel, long tag) {

        try {
            executorRecovery.execute(
                new UploadStatus(message, channel, tag),
                uploadRepository.submit(message, channel, tag)
            );
        } catch (RejectedExecutionException e) { // Thread Exception
            log.error("Upload rejected: {}", e.getMessage(), e);
            statusRepository.findById(message.getKey()).ifPresent(status -> status.setError("Upload rejected due to server load"));
            throw new RuntimeException("Server is busy, please try again later", e);
        }
    }

//    public void upload(ConvertRequest message, Channel channel, long tag) {
//        try {
//            String key = message.getKey();
//            activeUploads.put(key, new UploadStatus(message, channel, tag));
//
//            executor.submit(() -> {
//                try {
//                    // 작업 수행
//                    uploadRepository.submit(message, channel, tag).run();
//                } finally {
//                    activeUploads.remove(key);
//                }
//            });
//        } catch (RejectedExecutionException e) { // Thread Exception
//            log.error("Upload rejected: {}", e.getMessage(), e);
//            statusRepository.findById(message.getKey()).ifPresent(status -> status.setError("Upload rejected due to server load"));
//            throw new RuntimeException("Server is busy, please try again later", e);
//        }
//    }

}
