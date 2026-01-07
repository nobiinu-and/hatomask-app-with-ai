package com.hatomask.infrastructure.repository;

import com.hatomask.domain.model.StoredPhoto;
import com.hatomask.domain.repository.StoredPhotoRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryStoredPhotoRepository implements StoredPhotoRepository {

    private final ConcurrentHashMap<UUID, StoredPhoto> store = new ConcurrentHashMap<>();

    @Override
    public void save(StoredPhoto photo) {
        if (photo == null) {
            throw new IllegalArgumentException("photo is required");
        }
        store.put(photo.photoId(), photo);
    }

    @Override
    public Optional<StoredPhoto> findValidById(UUID photoId, Clock clock) {
        if (photoId == null) {
            throw new IllegalArgumentException("photoId is required");
        }
        if (clock == null) {
            throw new IllegalArgumentException("clock is required");
        }

        StoredPhoto photo = store.get(photoId);
        if (photo == null) {
            return Optional.empty();
        }

        if (photo.isExpired(clock)) {
            store.remove(photoId);
            return Optional.empty();
        }

        return Optional.of(photo);
    }

    @Override
    public void delete(UUID photoId) {
        if (photoId == null) {
            throw new IllegalArgumentException("photoId is required");
        }
        store.remove(photoId);
    }
}
