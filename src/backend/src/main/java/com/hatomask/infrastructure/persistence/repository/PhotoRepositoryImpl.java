package com.hatomask.infrastructure.persistence.repository;

import com.hatomask.domain.model.ContentType;
import com.hatomask.domain.model.FileSize;
import com.hatomask.domain.model.Photo;
import com.hatomask.domain.repository.PhotoRepository;
import com.hatomask.infrastructure.persistence.entity.PhotoEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * PhotoRepositoryの実装クラス.
 */
@Repository
public class PhotoRepositoryImpl implements PhotoRepository {

    private final JpaPhotoRepository jpaPhotoRepository;

    /**
     * コンストラクタ.
     *
     * @param jpaPhotoRepository Spring Data JPAリポジトリ
     */
    public PhotoRepositoryImpl(JpaPhotoRepository jpaPhotoRepository) {
        this.jpaPhotoRepository = jpaPhotoRepository;
    }

    @Override
    public Photo save(Photo photo) {
        PhotoEntity entity = toEntity(photo);
        PhotoEntity saved = jpaPhotoRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Photo> findById(UUID id) {
        return jpaPhotoRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public void deleteById(UUID id) {
        jpaPhotoRepository.deleteById(id);
    }

    private PhotoEntity toEntity(Photo photo) {
        return new PhotoEntity(
                photo.getId(),
                photo.getOriginalFileName(),
                photo.getContentType().getValue(),
                photo.getFileSize().getBytes(),
                photo.getImageData(),
                photo.getCreatedAt(),
                photo.getUpdatedAt()
        );
    }

    private Photo toDomain(PhotoEntity entity) {
        return new Photo(
                entity.getId(),
                entity.getOriginalFileName(),
                new ContentType(entity.getContentType()),
                new FileSize(entity.getFileSize()),
                entity.getImageData(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
