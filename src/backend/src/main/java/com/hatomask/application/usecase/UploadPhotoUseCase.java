package com.hatomask.application.usecase;

import com.hatomask.application.exception.ImageDecodingException;
import com.hatomask.application.exception.InvalidFileException;
import com.hatomask.application.exception.PayloadTooLargeException;
import com.hatomask.application.exception.UnsupportedMediaTypeException;
import com.hatomask.domain.model.FileSizeBytes;
import com.hatomask.domain.model.ImageDimensions;
import com.hatomask.domain.model.MimeType;
import com.hatomask.domain.model.UploadedPhotoData;
import com.hatomask.domain.model.UploadedPhotoReference;
import com.hatomask.domain.repository.UploadedPhotoDataRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UploadPhotoUseCase {

    private static final int EXPIRES_IN_MINUTES = 5;

    private final Clock clock;
    private final UploadedPhotoDataRepository uploadedPhotoDataRepository;

    public UploadPhotoUseCase(Clock clock, UploadedPhotoDataRepository uploadedPhotoDataRepository) {
        this.clock = clock;
        this.uploadedPhotoDataRepository = uploadedPhotoDataRepository;
    }

    public UploadedPhotoReference execute(MultipartFile file) {
        requireValidFile(file);
        long size = requireAcceptableSize(file);
        FileSizeBytes fileSizeBytes = FileSizeBytes.of(size);
        byte[] bytes = readBytes(file);
        MimeType mimeType = parseMimeType(file);
        ImageDimensions dimensions = decodeDimensions(bytes);

        UUID photoId = UUID.randomUUID();
        OffsetDateTime expiresAt = OffsetDateTime.now(clock).plusMinutes(EXPIRES_IN_MINUTES);

        uploadedPhotoDataRepository.save(new UploadedPhotoData(
                photoId,
                expiresAt,
                mimeType,
                dimensions,
                fileSizeBytes,
                bytes));

        return UploadedPhotoReference.create(
                photoId,
                expiresAt,
                mimeType,
                dimensions,
                fileSizeBytes,
                clock);
    }

    private static void requireValidFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new InvalidFileException("file is required");
        }
    }

    private static long requireAcceptableSize(MultipartFile file) {
        long size = file.getSize();
        if (size > FileSizeBytes.MAX_BYTES) {
            throw new PayloadTooLargeException("payload too large");
        }
        return size;
    }

    private static byte[] readBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new ImageDecodingException("failed to read image");
        }
    }

    private static MimeType parseMimeType(MultipartFile file) {
        try {
            return MimeType.fromValue(file.getContentType());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedMediaTypeException("unsupported media type");
        }
    }

    private static ImageDimensions decodeDimensions(byte[] bytes) {
        BufferedImage image;
        try (InputStream inputStream = new ByteArrayInputStream(bytes)) {
            image = ImageIO.read(inputStream);
        } catch (IOException ex) {
            throw new ImageDecodingException("failed to read image");
        }

        if (image == null) {
            throw new ImageDecodingException("failed to decode image");
        }

        return ImageDimensions.of(image.getWidth(), image.getHeight());
    }
}
