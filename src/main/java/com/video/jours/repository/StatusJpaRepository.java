package com.video.jours.repository;

import com.video.jours.entity.VideoStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StatusJpaRepository extends JpaRepository<VideoStatus, String> {
}
