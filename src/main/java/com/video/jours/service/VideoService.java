package com.video.jours.service;

import com.jours.easy_ffmpeg.FFmpegConverter;
import com.video.jours.component.PathManager;
import com.video.jours.dto.serializable.ConvertRequest;
import com.video.jours.enums.PathType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoService {

    private final FFmpegConverter fFmpegConverter;
    private final PathManager pathManager;

    public Path generateHls(File video, ConvertRequest message) throws IOException {
        Path storePath = Files.createDirectories(pathManager.get(PathType.VIDEO, message.getKey()));
        return fFmpegConverter.convertToHls(storePath, video);
    }

}
