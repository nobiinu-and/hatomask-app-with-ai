package com.hatomask.integration;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.usecase.DetectFaceLandmarksUseCase;
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
@DisplayName("FaceDetectionController 統合テスト（顔なし）")
class FaceDetectionControllerNoFaceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DetectFaceLandmarksUseCase detectFaceLandmarksUseCase;

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は顔が検出できない場合 422 を返す")
    void postFaceDetections_NoFace_ReturnsUnprocessableEntity() throws Exception {
        UUID photoId = UUID.randomUUID();
        when(detectFaceLandmarksUseCase.execute(eq(photoId)))
                .thenThrow(new FaceNotDetectedException("face not detected"));

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType(MediaType.valueOf("application/problem+json")))
                .andExpect(jsonPath("$.title").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.status").value(422));
    }
}
