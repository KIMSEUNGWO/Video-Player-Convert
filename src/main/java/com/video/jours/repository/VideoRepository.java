package com.video.jours.repository;

import com.video.jours.component.DirectoryManager;
import com.video.jours.component.PathManager;
import com.video.jours.component.ffmpeg.FFmpegManager;
import com.video.jours.enums.PathType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Repository
@RequiredArgsConstructor
public class VideoRepository {
    private final PathManager pathManager;
    private final FFmpegManager ffmpegManager;
    private final DirectoryManager directoryManager;

    public Path processVideo(File video, String videoId) throws IOException {

        Path videoPath = null;

        try {
            videoPath = Files.createDirectories(pathManager.get(PathType.VIDEO, videoId));
            Process process = ffmpegManager.convert(videoPath, video.toPath());

            printLog(process);

            // 프로세스 완료 대기
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("FFmpeg process failed with exit code: " + exitCode);
            }
        } catch (Exception e) {
            if (videoPath != null) {
                directoryManager.deleteIfExists(videoPath);
            }
            throw new IOException(e);
        }
        return videoPath;
    }

    private void printLog(Process process) throws IOException {
        // FFmpeg 출력 로그 확인
        try (var reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            reader.lines().forEach(line -> System.out.println("FFmpeg: " + line));
        }
    }

}
