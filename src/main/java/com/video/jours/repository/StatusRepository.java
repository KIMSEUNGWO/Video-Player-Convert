package com.video.jours.repository;

import com.video.jours.entity.VideoStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class StatusRepository {

    private final StatusJpaRepository statusJpaRepository;

    public Optional<VideoStatus> findById(String videoId) {
        return statusJpaRepository.findById(videoId);
    }

    public void save(VideoStatus saveVideoStatus) {
        statusJpaRepository.save(saveVideoStatus);
    }
}
