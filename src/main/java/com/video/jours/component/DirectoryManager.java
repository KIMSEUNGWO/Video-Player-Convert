package com.video.jours.component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;

@Slf4j
@Component
@RequiredArgsConstructor
public class DirectoryManager {

    private final SimpleFileVisitor<Path> deleteVisitor;

    public void deleteIfExists(Path path) {
        try {
            if (Files.isDirectory(path)) {
                Files.walkFileTree(path, deleteVisitor);
            } else {
                Files.deleteIfExists(path);
            }
        } catch (IOException e) {
            log.error("Failed to delete: {}", path, e);
        }
    }

}
