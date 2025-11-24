package com.hatomask.application.usecase;

import com.hatomask.infrastructure.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadPhotoUseCase {

    private final StorageService storageService;

    public UploadPhotoUseCase(StorageService storageService) {
        this.storageService = storageService;
    }

    public String store(MultipartFile file) throws IOException {
        return storageService.store(file);
    }
}
