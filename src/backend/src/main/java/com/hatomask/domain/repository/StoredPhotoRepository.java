package com.hatomask.domain.repository;

import com.hatomask.domain.model.StoredPhoto;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

public interface StoredPhotoRepository {
    void save(StoredPhoto photo);

    Optional<StoredPhoto> findValidById(UUID photoId, Clock clock);

    void delete(UUID photoId);
}
