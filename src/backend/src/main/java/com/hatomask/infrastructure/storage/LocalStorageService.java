package com.hatomask.infrastructure.storage;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class LocalStorageService implements StorageService {

    private final Path uploadDir;

    public LocalStorageService(@Value("${app.upload.dir:uploads}") String uploadDir) {
        this.uploadDir = Path.of(uploadDir);
        try {
            if (!Files.exists(this.uploadDir)) {
                Files.createDirectories(this.uploadDir);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create upload directory: " + this.uploadDir, e);
        }
    }

    @Override
    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("file is empty");
        }
        String original = Path.of(file.getOriginalFilename()).getFileName().toString();
        String ext = "";
        int dot = original.lastIndexOf('.');
        if (dot >= 0) {
            ext = original.substring(dot);
        }
        String uniqueName = java.util.UUID.randomUUID().toString() + ext;
        Path target = uploadDir.resolve(uniqueName);
        try (var in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return target.getFileName().toString();
    }
}
