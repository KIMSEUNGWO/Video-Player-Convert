package com.video.jours.component;

import com.video.jours.enums.PathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LocalPathManager implements PathManager {

    @Value("${storage.local.directory.original}")
    private String originalDirectory;
    @Value("${storage.local.directory.video}")
    private String videoDirectory;
    @Value("${storage.local.directory.image}")
    private String imageDirectory;


    @Override
    public Path get(PathType type, String key) {
        return switch (type) {
            case VIDEO -> Paths.get(videoDirectory + "/" + key);
            case ORIGINAL_VIDEO -> Paths.get(originalDirectory + "/" + key);
            case THUMBNAIL -> Paths.get(imageDirectory + "/" + key);
        };
    }
}
