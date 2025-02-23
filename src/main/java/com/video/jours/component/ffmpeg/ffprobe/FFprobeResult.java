package com.video.jours.component.ffmpeg.ffprobe;

import com.video.jours.component.ffmpeg.AudioStream;
import com.video.jours.component.ffmpeg.VideoStream;

public record FFprobeResult(
    int width,
    int height,
    double fps,
    boolean hasAudio
) {

    public VideoStream toVideoStream() {
        return new VideoStream(width, height, fps);
    }
    public AudioStream toAudioStream() {
        return new AudioStream(hasAudio);
    }
}
