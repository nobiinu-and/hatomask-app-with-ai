package com.hatomask.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("FaceDetection API 統合テスト")
class FaceDetectionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は存在しない photoId の場合 404 + application/problem+json")
    void postFaceDetections_UnknownPhotoId_ReturnsNotFoundProblemDetails() throws Exception {
        UUID photoId = UUID.randomUUID();

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は不正なUUIDの場合 400 + application/problem+json")
    void postFaceDetections_InvalidUuid_ReturnsBadRequestProblemDetails() throws Exception {
        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("photoId"));
    }
}
