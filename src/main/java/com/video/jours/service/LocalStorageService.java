package com.video.jours.service;

import com.video.jours.component.DirectoryManager;
import com.video.jours.enums.PathType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class LocalStorageService implements StorageService {

    private final DirectoryManager directoryManager;
    private final String storageAddress;
    private final RestClient restClient;

    public LocalStorageService(@Value("${storage.server.address}") String address, DirectoryManager directoryManager) {
        this.storageAddress = address;
        this.directoryManager = directoryManager;
        this.restClient = RestClient.create(address);
    }

    @Override
    public File download(PathType type, String key) throws IOException {
        File tempFile = File.createTempFile("download-", "-" + key);

        try {
            // Storage Server의 리소스 URL 생성
            String resourceUrl = String.format("%s/original/%s", storageAddress, key);
            Resource resource = new UrlResource(resourceUrl);

            // Resource에서 파일로 복사
            Files.copy(resource.getInputStream(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return tempFile;

        } catch (Exception e) {
            tempFile.deleteOnExit();
            throw new IOException("Failed to download file: " + e.getMessage(), e);
        }
    }

    @Override
    public void uploadVideo(Path hlsDirectory) throws IOException {
        // 임시 ZIP 파일 생성
        File zipFile = File.createTempFile("hls-", ".zip");
        try {

            // HLS 디렉토리를 ZIP으로 압축
            try (ZipOutputStream zipOut = new ZipOutputStream(new FileOutputStream(zipFile))) {
                String folderName = hlsDirectory.getFileName().toString();  // 최상위 폴더명

                // 먼저 폴더 엔트리 추가
                zipOut.putNextEntry(new ZipEntry(folderName + "/"));
                zipOut.closeEntry();

                Files.walk(hlsDirectory)
                    .filter(path -> !Files.isDirectory(path))
                    .forEach(path -> {
                        try {
                            // folderName/나머지경로 형태로 생성
                            String entryPath = folderName + "/" + hlsDirectory.relativize(path);
                            ZipEntry zipEntry = new ZipEntry(entryPath);
                            zipOut.putNextEntry(zipEntry);
                            Files.copy(path, zipOut);
                            zipOut.closeEntry();
                        } catch (IOException e) {
                            throw new UncheckedIOException(e);
                        }
                    });
            }

            // ZIP 파일 전송
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", new FileSystemResource(zipFile));

            restClient.post()
                .uri("/api/v1/upload/hls")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(body)
                .retrieve()
                .body(String.class);
        } catch (RestClientException e) {
            throw new IOException("Upload failed", e);
        } finally {
            zipFile.deleteOnExit();
            directoryManager.deleteIfExists(hlsDirectory);
        }
    }

    @Override
    public void delete(PathType type, String key) {
        restClient.delete()
            .uri(builder -> builder
                .path("/api/v1/delete")
                .queryParam("pathType", type)
                .queryParam("storeKey", key)
                .build()
            )
            .retrieve()
            .onStatus(response -> false)
            .toEntity(String.class);
    }


    private MultiValueMap<String, Object> fileWrapper(File file) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", new FileSystemResource(file));
        return body;
    }

}
