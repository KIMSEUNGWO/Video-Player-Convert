package com.video.jours.service;

import com.video.jours.enums.PathType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

public interface StorageService {

    File download(PathType type, String key) throws IOException;
    void uploadVideo(Path path) throws IOException;
    void delete(PathType type, String fileId);
}
