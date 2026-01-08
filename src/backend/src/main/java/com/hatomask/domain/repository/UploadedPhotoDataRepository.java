package com.hatomask.domain.repository;

import com.hatomask.domain.model.UploadedPhotoData;

import java.util.Optional;
import java.util.UUID;

public interface UploadedPhotoDataRepository {
    void save(UploadedPhotoData data);

    Optional<UploadedPhotoData> findByPhotoId(UUID photoId);

    void delete(UUID photoId);
}
