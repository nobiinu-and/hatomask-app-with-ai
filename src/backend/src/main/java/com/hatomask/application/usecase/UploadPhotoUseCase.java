package com.hatomask.application.usecase;

import com.hatomask.application.exception.ImageDecodingException;
import com.hatomask.application.exception.InvalidFileException;
import com.hatomask.application.exception.PayloadTooLargeException;
import com.hatomask.application.exception.UnsupportedMediaTypeException;
import com.hatomask.domain.model.FileSizeBytes;
import com.hatomask.domain.model.ImageDimensions;
import com.hatomask.domain.model.MimeType;
import com.hatomask.domain.model.UploadedPhotoReference;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadPhotoUseCase {

    private static final int EXPIRES_IN_MINUTES = 5;

    private final Clock clock;

    public UploadPhotoUseCase(Clock clock) {
        this.clock = clock;
    }

    public UploadedPhotoReference execute(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("file is required");
        }

        long size = file.getSize();
        if (size > FileSizeBytes.MAX_BYTES) {
            throw new PayloadTooLargeException("payload too large");
        }

        MimeType mimeType;
        try {
            mimeType = MimeType.fromValue(file.getContentType());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedMediaTypeException("unsupported media type");
        }

        BufferedImage image;
        try (InputStream inputStream = file.getInputStream()) {
            image = ImageIO.read(inputStream);
        } catch (IOException ex) {
            throw new ImageDecodingException("failed to read image");
        }

        if (image == null) {
            throw new ImageDecodingException("failed to decode image");
        }

        ImageDimensions dimensions = ImageDimensions.of(image.getWidth(), image.getHeight());

        UUID photoId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now(clock).plusMinutes(EXPIRES_IN_MINUTES);

        return UploadedPhotoReference.create(
                photoId,
                expiresAt,
                mimeType,
                dimensions,
                FileSizeBytes.of(size),
                clock);
    }
}
