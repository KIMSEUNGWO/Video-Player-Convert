package com.video.jours.service;

import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.entity.VideoStatus;
import com.video.jours.enums.ProcessingStatus;
import com.video.jours.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class StatusService {

    private final StatusRepository statusRepository;

    @Transactional
    public void uploadStatus(ConvertRequest message) {
        statusRepository.save(VideoStatus.builder()
            .statusKey(message.getKey())
            .status(ProcessingStatus.PENDING)
            .thumbnail(message.getThumbnail())
            .originalVideo(message.getOriginalVideo())
            .title(message.getTitle())
            .build());
    }
}
