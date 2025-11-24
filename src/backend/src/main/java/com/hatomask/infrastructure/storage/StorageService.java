package com.hatomask.infrastructure.storage;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface StorageService {
    /**
     * Store the given multipart file and return the stored filename.
     */
    String store(MultipartFile file) throws IOException;
}
