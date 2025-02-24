package com.video.jours.component.ffmpeg;

import com.video.jours.component.ffmpeg.ffprobe.FFprobeManager;
import com.video.jours.component.ffmpeg.ffprobe.FFprobeResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class FFmpegManagerImpl implements FFmpegManager {

    @Value("${path.ffmpeg}")
    private String ffmpegPath;

    private final FFprobeManager ffprobeManager;

    @Override
    public Process convert(Path videoPath, Path originalVideoPath) throws IOException {

        FFprobeResult analyze = ffprobeManager.analyze(originalVideoPath);

        var command = FFmpegCommand.newCommand(ffmpegPath, videoPath, originalVideoPath, analyze)
            .buildCommand();

        return new ProcessBuilder(command)
            .redirectErrorStream(true)
            .start();
    }

}
