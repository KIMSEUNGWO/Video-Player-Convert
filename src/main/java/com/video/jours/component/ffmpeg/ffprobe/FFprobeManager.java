package com.video.jours.component.ffmpeg.ffprobe;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FFprobeManager {

    private final ObjectMapper mapper;

    @Value("${path.ffprobe}")
    private String ffprobePath;

    public FFprobeResult analyze(Path originalVideoPath) throws IOException {

        try {
            // FFprobe 실행
            Process process = convert(originalVideoPath);

            // 결과 읽기
            String json = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            // 프로세스 완료 대기
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("FFprobe failed with exit code: " + exitCode);
            }

            return parseFFprobeResult(json);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("FFprobe process interrupted", e);
        }
    }

    private Process convert(Path originalVideoPath) throws IOException {
        List<String> command = List.of(
            ffprobePath,
            "-v", "error",
            "-show_entries", "stream=codec_type,width,height,r_frame_rate",
            "-of", "json",
            originalVideoPath.toString()
        );

        return new ProcessBuilder(command)
            .redirectErrorStream(true)
            .start();
    }

    private FFprobeResult parseFFprobeResult(String json) throws IOException {
        JsonNode root = mapper.readTree(json);
        JsonNode streams = root.path("streams");

        // 비디오 스트림 찾기
        JsonNode videoStream = null;
        boolean hasAudio = false;

        for (JsonNode stream : streams) {
            String codecType = stream.path("codec_type").asText();
            if ("video".equals(codecType)) {
                videoStream = stream;
            } else if ("audio".equals(codecType)) {
                hasAudio = true;
            }
        }

        if (videoStream == null) {
            throw new IOException("No video stream found");
        }

        int width = videoStream.path("width").asInt();
        int height = videoStream.path("height").asInt();
        String frameRate = videoStream.path("r_frame_rate").asText();
        double fps = calculateFps(frameRate);

        return new FFprobeResult(width, height, fps, hasAudio);
    }

    private double calculateFps(String frameRate) {
        String[] parts = frameRate.split("/");
        if (parts.length == 2) {
            double numerator = Double.parseDouble(parts[0]);
            double denominator = Double.parseDouble(parts[1]);
            return numerator / denominator;
        }
        return Double.parseDouble(frameRate);
    }
}
