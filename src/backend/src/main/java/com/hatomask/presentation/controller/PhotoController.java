package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.PhotoResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 写真アップロード・ダウンロード機能のController (Stub実装)
 * 
 * Phase 5: Stub実装 - 固定データを返す
 * Phase 6: 本実装 - ドメイン層・UseCase実装後に置き換える
 */
@RestController
@RequestMapping("/api/v1/photos")
public class PhotoController {

    private static final DateTimeFormatter ISO_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    /**
     * 写真をアップロード (Stub実装)
     * 
     * OpenAPI: POST /api/v1/photos
     * 
     * TODO: Replace stub implementation
     * Phase 6でドメイン層・UseCase実装後に置き換える
     * 
     * @param file アップロードする写真ファイル
     * @return PhotoResponse (id, originalFileName, contentType, fileSize, createdAt, updatedAt)
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PhotoResponse> uploadPhoto(
            @RequestParam("file") MultipartFile file) {
        
        // TODO: Replace stub implementation
        // スタブ実装: 固定UUIDと現在時刻を返す
        UUID photoId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(ISO_FORMATTER);
        
        PhotoResponse response = new PhotoResponse(
            photoId.toString(),
            file.getOriginalFilename(),
            file.getContentType(),
            file.getSize(),
            timestamp,
            timestamp
        );
        
        URI location = URI.create("/api/v1/photos/" + photoId);
        return ResponseEntity.created(location).body(response);
    }

    /**
     * 写真を取得 (Stub実装)
     * 
     * OpenAPI: GET /api/v1/photos/{id}
     * 
     * TODO: Replace stub implementation
     * Phase 6でドメイン層・UseCase実装後に置き換える
     * 
     * @param id 写真ID (UUID)
     * @param download ダウンロード用にContent-Dispositionヘッダーを設定するか
     * @return 画像バイナリデータ
     */
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> getPhotoById(
            @PathVariable String id,
            @RequestParam(required = false, defaultValue = "false") boolean download) {
        
        // TODO: Replace stub implementation
        // スタブ実装: 空のバイト配列を返す
        // 実装時は実際の画像バイナリをPhotoRepositoryから取得して返す
        
        byte[] emptyImage = new byte[0];
        
        if (download) {
            // ダウンロード用: Content-Dispositionヘッダーを設定
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header("Content-Disposition", "attachment; filename=\"photo_" + id + ".jpg\"")
                .body(emptyImage);
        } else {
            // プレビュー用: 画像データのみ
            return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(emptyImage);
        }
    }
}
