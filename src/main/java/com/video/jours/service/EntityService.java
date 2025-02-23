package com.video.jours.service;

import com.video.jours.entity.Video;
import com.video.jours.entity.VideoStatus;
import com.video.jours.repository.VideoJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class EntityService {

    private final VideoJpaRepository videoJpaRepository;

    @Transactional
    public void saveVideoEntity(String videoId, VideoStatus status) {
        Video saveVideo = Video.builder()
            .title(status.getTitle())
            .videoId(videoId)
            .thumbnail(status.getThumbnail())
            .build();
        videoJpaRepository.save(saveVideo);
    }
}
