package com.hatomask.infrastructure.repository;

import com.hatomask.domain.model.UploadedPhotoData;
import com.hatomask.domain.repository.UploadedPhotoDataRepository;
import org.springframework.stereotype.Repository;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUploadedPhotoDataRepository implements UploadedPhotoDataRepository {

    private final ConcurrentHashMap<UUID, UploadedPhotoData> store = new ConcurrentHashMap<>();
    private final Clock clock;

    public InMemoryUploadedPhotoDataRepository(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void save(UploadedPhotoData data) {
        store.put(data.photoId(), data);
    }

    @Override
    public Optional<UploadedPhotoData> findByPhotoId(UUID photoId) {
        UploadedPhotoData data = store.get(photoId);
        if (data == null) {
            return Optional.empty();
        }

        OffsetDateTime now = OffsetDateTime.now(clock);
        if (data.expiresAt().isBefore(now)) {
            store.remove(photoId);
            return Optional.empty();
        }

        return Optional.of(data);
    }

    @Override
    public void delete(UUID photoId) {
        store.remove(photoId);
    }
}
