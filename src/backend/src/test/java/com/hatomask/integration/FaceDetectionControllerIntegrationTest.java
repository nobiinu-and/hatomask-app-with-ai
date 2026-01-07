package com.hatomask.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("FaceDetectionController 統合テスト")
class FaceDetectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は 200 でランドマークを返す")
    void postFaceDetections_ReturnsOk() throws Exception {
        byte[] pngBytes = createPngBytes(10, 10);
        MockMultipartFile file = new MockMultipartFile("file", "photo.png", "image/png", pngBytes);

        MvcResult uploadResult = mockMvc.perform(multipart("/api/v1/photos").file(file))
                .andExpect(status().isCreated())
                .andReturn();

        UUID photoId = extractPhotoId(uploadResult);

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.landmarks").isArray())
                .andExpect(jsonPath("$.result.landmarks.length()").value(org.hamcrest.Matchers.greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.result.boundingBox.xMin").exists())
                .andExpect(jsonPath("$.result.boundingBox.yMin").exists())
                .andExpect(jsonPath("$.result.boundingBox.width").exists())
                .andExpect(jsonPath("$.result.boundingBox.height").exists());
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は存在しない photoId の場合 404 を返す")
    void postFaceDetections_UnknownId_ReturnsNotFound() throws Exception {
        UUID unknown = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", unknown))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    private UUID extractPhotoId(MvcResult result) throws Exception {
        String content = result.getResponse().getContentAsString();
        JsonNode node = objectMapper.readTree(content);
        return UUID.fromString(node.get("photoId").asText());
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
