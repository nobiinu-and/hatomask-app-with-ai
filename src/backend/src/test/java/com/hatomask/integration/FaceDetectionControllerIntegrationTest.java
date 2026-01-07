package com.hatomask.integration;

import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.application.usecase.DetectFaceLandmarksUseCase;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
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

    @MockBean
    private DetectFaceLandmarksUseCase detectFaceLandmarksUseCase;

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は 200 と検出結果を返す")
    void postFaceDetections_ReturnsOk() throws Exception {
        UUID photoId = UUID.randomUUID();

        java.util.ArrayList<FaceLandmark> landmarks = new java.util.ArrayList<>(68);
        for (int i = 0; i < 68; i++) {
            landmarks.add(new FaceLandmark(0.2, 0.3));
        }

        FaceDetectionResult result = new FaceDetectionResult(
                landmarks,
                new FaceBoundingBox(0.1, 0.1, 0.2, 0.3),
                0.5);

        when(detectFaceLandmarksUseCase.execute(eq(photoId))).thenReturn(result);

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.result.landmarks").isArray())
                .andExpect(jsonPath("$.result.landmarks.length()").value(68))
                .andExpect(jsonPath("$.result.boundingBox.xMin").exists())
                .andExpect(jsonPath("$.result.boundingBox.yMin").exists())
                .andExpect(jsonPath("$.result.boundingBox.width").exists())
                .andExpect(jsonPath("$.result.boundingBox.height").exists())
                .andExpect(jsonPath("$.result.confidence").exists());
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は存在しない photoId の場合 404 を返す")
    void postFaceDetections_PhotoNotFound_ReturnsNotFound() throws Exception {
        UUID unknown = UUID.randomUUID();
        when(detectFaceLandmarksUseCase.execute(eq(unknown))).thenThrow(new PhotoNotFoundException("photo not found"));

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", unknown))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }
}
