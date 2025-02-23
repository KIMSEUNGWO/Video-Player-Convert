package com.video.jours.repository;

import com.video.jours.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VideoJpaRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByVideoId(String videoId);
}
