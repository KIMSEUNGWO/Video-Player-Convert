package com.video.jours.config;

import com.jours.easy_ffmpeg.config.FFmpegConfiguration;
import com.jours.easy_ffmpeg.config.HlsConvertBuilder;
import com.jours.easy_ffmpeg.config.HlsConvertConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Paths;

@Configuration
@RequiredArgsConstructor
public class FFmpegConfig implements FFmpegConfiguration {

    @Override
    public HlsConvertConfig hlsConfig(HlsConvertBuilder builder) {
        return builder.debug()
            .vodPreset()
            .enableEncryption(encryptionKeyBuilder -> encryptionKeyBuilder
                .keyInfoPath(Paths.get("src/main/resources/key_info.txt"))
                .keyFilePath(Paths.get("src/main/resources/encryption.key"))
                .keyServerPath("http://localhost:8080/key.bin")
                .keySize(16)
                .fileReplace(true)
            )
            .build();
    }

}
