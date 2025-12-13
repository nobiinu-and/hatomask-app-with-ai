package com.hatomask.application.usecase;

import com.hatomask.domain.model.Photo;
import com.hatomask.domain.repository.PhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * 写真取得のユースケース.
 */
@Service
public class GetPhotoUseCase {

    private final PhotoRepository photoRepository;

    /**
     * コンストラクタ.
     *
     * @param photoRepository 写真リポジトリ
     */
    public GetPhotoUseCase(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    /**
     * IDで写真を取得する.
     *
     * @param id 写真のUUID
     * @return 写真エンティティ
     * @throws PhotoNotFoundException 写真が見つからない場合
     */
    @Transactional(readOnly = true)
    public Photo execute(UUID id) {
        return photoRepository.findById(id)
                .orElseThrow(() -> new PhotoNotFoundException("ID '" + id + "' の写真が見つかりません"));
    }
}
