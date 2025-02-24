package com.video.jours.service;

import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final VideoRepository videoRepository;

    public Path generateHls(File video, ConvertRequest message) throws IOException {
        return videoRepository.processVideo(video, message.getKey());
    }


}
