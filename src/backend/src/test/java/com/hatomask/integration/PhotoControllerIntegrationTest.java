package com.hatomask.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("PhotoController 統合テスト")
class PhotoControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/photos は有効なPNGで 201 Created を返す")
    void postPhotos_ValidPng_ReturnsCreated() throws Exception {
        byte[] pngBytes = createPngBytes(2, 3);
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", pngBytes);

        mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location",
                        org.hamcrest.Matchers.matchesPattern("/api/v1/photos/[0-9a-f\\-]{36}")))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.photoId").exists())
                .andExpect(jsonPath("$.mimeType").value("image/png"))
                .andExpect(jsonPath("$.fileSizeBytes").value(pngBytes.length))
                .andExpect(jsonPath("$.dimensions.width").value(2))
                .andExpect(jsonPath("$.dimensions.height").value(3))
                .andExpect(jsonPath("$.expiresAt").exists());
    }

    @Test
    @DisplayName("POST /api/v1/photos は file が空の場合 400 + application/problem+json を返す")
    void postPhotos_EmptyFile_ReturnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", new byte[0]);

        mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("file"));
    }

    @Test
    @DisplayName("POST /api/v1/photos はサイズ超過の場合 413 + application/problem+json を返す")
    void postPhotos_TooLarge_ReturnsPayloadTooLarge() throws Exception {
        byte[] bytes = new byte[(10 * 1024 * 1024) + 1];
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", bytes);

        mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isPayloadTooLarge())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Payload Too Large"))
                .andExpect(jsonPath("$.status").value(413));
    }

    @Test
    @DisplayName("POST /api/v1/photos は未対応形式の場合 415 + application/problem+json を返す")
    void postPhotos_UnsupportedMediaType_ReturnsUnsupportedMediaType() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "photo.gif", "image/gif", new byte[] { 1, 2, 3 });

        mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Unsupported Media Type"))
                .andExpect(jsonPath("$.status").value(415))
                .andExpect(jsonPath("$.errors[0].field").value("file"));
    }

    @Test
    @DisplayName("POST /api/v1/photos はデコードできない場合 400 + application/problem+json を返す")
    void postPhotos_DecodeFail_ReturnsBadRequest() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "photo.jpg", "image/jpeg", new byte[] { 1, 2, 3 });

        mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400));
    }

    private static byte[] createPngBytes(int width, int height) throws Exception {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        boolean ok = ImageIO.write(image, "png", out);
        if (!ok) {
            throw new IllegalStateException("failed to create png bytes");
        }
        return out.toByteArray();
    }
}
