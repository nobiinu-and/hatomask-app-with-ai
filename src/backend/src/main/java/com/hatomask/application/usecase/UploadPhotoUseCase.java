package com.hatomask.application.usecase;

import com.hatomask.domain.model.ContentType;
import com.hatomask.domain.model.FileSize;
import com.hatomask.domain.model.Photo;
import com.hatomask.domain.repository.PhotoRepository;
import com.hatomask.presentation.dto.PhotoResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 写真アップロードのユースケース.
 */
@Service
public class UploadPhotoUseCase {

    private final PhotoRepository photoRepository;

    /**
     * コンストラクタ.
     *
     * @param photoRepository 写真リポジトリ
     */
    public UploadPhotoUseCase(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    /**
     * 写真をアップロードする.
     *
     * @param originalFileName 元のファイル名
     * @param fileSizeBytes    ファイルサイズ（バイト）
     * @param contentTypeValue MIMEタイプ
     * @param imageData        画像データ
     * @return アップロードされた写真のメタデータ
     */
    @Transactional
    public PhotoResponse execute(String originalFileName, Long fileSizeBytes, 
                                  String contentTypeValue, byte[] imageData) {
        // ValueObjectを生成（バリデーションを含む）
        ContentType contentType = new ContentType(contentTypeValue);
        FileSize fileSize = new FileSize(fileSizeBytes);

        // Photoエンティティを生成
        LocalDateTime now = LocalDateTime.now();
        Photo photo = new Photo(
                UUID.randomUUID(),
                originalFileName,
                contentType,
                fileSize,
                imageData,
                now,
                now
        );

        // 保存
        Photo savedPhoto = photoRepository.save(photo);

        // DTOに変換して返却
        return new PhotoResponse(
                savedPhoto.getId().toString(),
                savedPhoto.getOriginalFileName(),
                savedPhoto.getContentType().getValue(),
                savedPhoto.getFileSize().getBytes(),
                savedPhoto.getCreatedAt(),
                savedPhoto.getUpdatedAt()
        );
    }
}
