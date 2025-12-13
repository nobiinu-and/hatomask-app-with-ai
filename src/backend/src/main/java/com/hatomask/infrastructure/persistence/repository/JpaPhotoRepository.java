package com.hatomask.infrastructure.persistence.repository;

import com.hatomask.infrastructure.persistence.entity.PhotoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Spring Data JPA PhotoRepository.
 */
@Repository
public interface JpaPhotoRepository extends JpaRepository<PhotoEntity, UUID> {
}
