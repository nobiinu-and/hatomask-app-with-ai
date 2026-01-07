package com.hatomask.application.usecase;

import com.hatomask.application.exception.ImageDecodingException;
import com.hatomask.application.exception.InvalidFileException;
import com.hatomask.application.exception.PayloadTooLargeException;
import com.hatomask.application.exception.UnsupportedMediaTypeException;
import com.hatomask.domain.model.FileSizeBytes;
import com.hatomask.domain.model.ImageDimensions;
import com.hatomask.domain.model.MimeType;
import com.hatomask.domain.model.StoredPhoto;
import com.hatomask.domain.model.UploadedPhotoReference;
import com.hatomask.domain.repository.StoredPhotoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadPhotoUseCase {

    private static final int EXPIRES_IN_MINUTES = 5;

    private final Clock clock;
    private final StoredPhotoRepository storedPhotoRepository;

    public UploadPhotoUseCase(Clock clock, StoredPhotoRepository storedPhotoRepository) {
        this.clock = clock;
        this.storedPhotoRepository = storedPhotoRepository;
    }

    public UploadedPhotoReference execute(MultipartFile file) {
        validateFilePresent(file);
        long size = validateSize(file);
        MimeType mimeType = parseMimeType(file);
        byte[] bytes = readBytes(file);
        ImageDimensions dimensions = decodeDimensions(bytes);
        UUID photoId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now(clock).plusMinutes(EXPIRES_IN_MINUTES);

        StoredPhoto storedPhoto = new StoredPhoto(
                photoId,
                expiresAt,
                mimeType,
                dimensions,
                FileSizeBytes.of(size),
                bytes);
        storedPhotoRepository.save(storedPhoto);

        return UploadedPhotoReference.create(
                photoId,
                expiresAt,
                mimeType,
                dimensions,
                FileSizeBytes.of(size),
                clock);
    }

    private static void validateFilePresent(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("file is required");
        }
    }

    private static long validateSize(MultipartFile file) {
        long size = file.getSize();
        if (size > FileSizeBytes.MAX_BYTES) {
            throw new PayloadTooLargeException("payload too large");
        }
        return size;
    }

    private static MimeType parseMimeType(MultipartFile file) {
        try {
            return MimeType.fromValue(file.getContentType());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedMediaTypeException("unsupported media type");
        }
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new ImageDecodingException("failed to read image");
        }
    }

    private static ImageDimensions decodeDimensions(byte[] bytes) {
        BufferedImage image;
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes)) {
            image = ImageIO.read(inputStream);
        } catch (IOException ex) {
            throw new ImageDecodingException("failed to decode image");
        }

        if (image == null) {
            throw new ImageDecodingException("failed to decode image");
        }

        return ImageDimensions.of(image.getWidth(), image.getHeight());
    }
}
