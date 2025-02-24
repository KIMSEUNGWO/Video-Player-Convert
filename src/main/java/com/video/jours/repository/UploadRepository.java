package com.video.jours.repository;

import com.rabbitmq.client.Channel;
import com.video.jours.component.ThreadTransactionManager;
import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.entity.VideoStatus;
import com.video.jours.service.EntityService;
import com.video.jours.service.StorageService;
import com.video.jours.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static com.video.jours.enums.PathType.ORIGINAL_VIDEO;
import static com.video.jours.enums.PathType.THUMBNAIL;
import static com.video.jours.enums.ProcessingStatus.COMPLETED;
import static com.video.jours.enums.ProcessingStatus.PROCESSING;

@Slf4j
@Repository
@RequiredArgsConstructor
public class UploadRepository {

    private final StorageService storageService;
    private final StatusRepository statusRepository;
    private final VideoService videoService;
    private final EntityService entityService;
    private final ThreadTransactionManager threadTransactionManager;

    public Runnable submit(ConvertRequest message, Channel channel, long tag) {
        return () -> {
            threadTransactionManager.updateStatus(message.getKey(), PROCESSING);
            File originalVideo = null;
            Path videoPath = null;
            try {
                originalVideo = storageService.download(ORIGINAL_VIDEO, message.getOriginalVideo());

                VideoStatus videoStatus = statusRepository.findById(message.getKey())
                    .orElseThrow(() -> new RuntimeException("Video status not found"));

                videoPath = videoService.generateHls(originalVideo, message);
                entityService.saveVideoEntity(message.getKey(), videoStatus);

                storageService.uploadVideo(videoPath);

                storageService.delete(ORIGINAL_VIDEO, message.getOriginalVideo());

                threadTransactionManager.updateStatus(message.getKey(), COMPLETED);
                // 성공적으로 처리 완료 시 ack 보내기
                channel.basicAck(tag, false);
            } catch (Exception e) {
                log.error("Error processing upload: {}", e.getMessage(), e);
                threadTransactionManager.updateStatus(message.getKey(), videoStatus -> {
                    storageService.delete(THUMBNAIL, videoStatus.getThumbnail());
                    storageService.delete(ORIGINAL_VIDEO, videoStatus.getOriginalVideo());
                    videoStatus.setError("Upload failed: " + e.getMessage());
                });
                try {
                    // 실패 시 nack 보내기 (재처리 요청 x)
                    channel.basicNack(tag, false, false);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            } finally {
                if (originalVideo != null) {
                    originalVideo.deleteOnExit();
                }
                if (videoPath != null) {
                    videoPath.toFile().deleteOnExit();
                }
            }
        };
    }

}
