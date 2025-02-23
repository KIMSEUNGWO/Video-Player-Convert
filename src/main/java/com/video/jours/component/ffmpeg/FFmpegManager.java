package com.video.jours.component.ffmpeg;

import java.io.IOException;
import java.nio.file.Path;

public interface FFmpegManager {

    Process convert(Path videoPath, Path originalVideoPath) throws IOException;
}
