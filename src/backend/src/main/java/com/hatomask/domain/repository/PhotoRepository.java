package com.hatomask.domain.repository;

import com.hatomask.domain.model.Photo;

import java.util.Optional;
import java.util.UUID;

/**
 * Photoエンティティのリポジトリインターフェース.
 */
public interface PhotoRepository {

    /**
     * Photoを保存する.
     *
     * @param photo 保存するPhoto
     * @return 保存されたPhoto
     */
    Photo save(Photo photo);

    /**
     * IDでPhotoを検索する.
     *
     * @param id 検索するUUID
     * @return Photoが存在する場合はOptional.of(photo)、存在しない場合はOptional.empty()
     */
    Optional<Photo> findById(UUID id);

    /**
     * IDでPhotoを削除する.
     *
     * @param id 削除するUUID
     */
    void deleteById(UUID id);
}
