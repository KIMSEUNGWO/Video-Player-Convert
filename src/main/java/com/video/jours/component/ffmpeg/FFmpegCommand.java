package com.video.jours.component.ffmpeg;

import com.video.jours.component.ffmpeg.ffprobe.FFprobeResult;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FFmpegCommand {

    private final String ffmpegPath;

    private final Path videoPath;
    private final Path originalVideoPath;

    private final VideoStream videoStream;
    private final AudioStream audioStream;

    public static FFmpegCommand newCommand(String ffmpegPath, Path videoPath, Path originalVideoPath, FFprobeResult probeResult) {
        return new FFmpegCommand(ffmpegPath, videoPath, originalVideoPath, probeResult.toVideoStream(), probeResult.toAudioStream());
    }


    public List<String> buildCommand() {
        List<String> command = new ArrayList<>();
        command.addAll(List.of(ffmpegPath, "-i", originalVideoPath.toString()));
        command.addAll(List.of("-filter_complex", buildFilterComplex()));
        command.addAll(buildMaps());
        command.addAll(List.of("-c:v", "libx264"));
        if (audioStream.exists()) {
            command.addAll(List.of("-c:a", "aac"));
        }
        command.addAll(buildBitrates());
        command.addAll(List.of(
            "-var_stream_map", buildVarStreamMap(),
            "-f", "hls",
            "-hls_time", "10",
            "-hls_list_size", "0",
            "-hls_segment_type", "mpegts",
            "-hls_segment_filename", videoPath + "/stream_%v/segment_%03d.ts",
            "-master_pl_name", "master.m3u8",
            videoPath + "/stream_%v/playlist.m3u8"
        ));

        return command;
    }


    private String buildFilterComplex() {
        List<String> scales = videoStream.getScales();

        String splitCount = String.format("[0:v]split=%d", scales.size());
        String outputs = scales.stream()
            .map(s -> String.format("[v%d]", scales.indexOf(s) + 1))
            .collect(Collectors.joining(""));

        return splitCount + outputs + ";" + String.join(";", scales);
    }

    private List<String> buildBitrates() {
        List<String> bitrates = new ArrayList<>();
        List<String> qualities = videoStream.getAvailableQualities();

        for (int i = 0; i < qualities.size(); i++) {
            bitrates.add("-b:v:" + i);
            bitrates.add(VideoStream.BITRATE.get(qualities.get(i)));
        }

        return bitrates;
    }

    private String buildVarStreamMap() {
        List<String> streamMaps = new ArrayList<>();
        List<String> qualities = videoStream.getAvailableQualities();

        for (int i = 0; i < qualities.size(); i++) {
            String quality = qualities.get(i);
            streamMaps.add(audioStream.exists()
                ? String.format("v:%d,a:%d,name:%sp", i, i, quality)
                : String.format("v:%d,name:%sp", i, quality));
        }

        return String.join(" ", streamMaps);
    }

    private List<String> buildMaps() {
        List<String> maps = new ArrayList<>();
        List<String> qualities = videoStream.getAvailableQualities();

        for (String quality : qualities) {
            maps.add("-map");
            maps.add(String.format("[v%sp]", quality));
            if (audioStream.exists()) {
                maps.add("-map");
                maps.add("0:a");
            }
        }
        return maps;
    }
}
