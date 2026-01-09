package com.hatomask.presentation.controller;

import com.hatomask.application.exception.FaceNotDetectedException;
import com.hatomask.application.exception.PhotoNotFoundException;
import com.hatomask.application.usecase.DetectFaceLandmarksUseCase;
import com.hatomask.application.usecase.UploadPhotoUseCase;
import com.hatomask.domain.model.FaceBoundingBox;
import com.hatomask.domain.model.FaceDetectionResult;
import com.hatomask.domain.model.FaceLandmark;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PhotoController.class)
@DisplayName("PhotoController(顔検出) 単体テスト")
class PhotoControllerFaceDetectionTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UploadPhotoUseCase uploadPhotoUseCase;

    @MockBean
    private DetectFaceLandmarksUseCase detectFaceLandmarksUseCase;

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は成功時 200 と FaceDetectionResponse を返す")
    void postFaceDetections_Success_ReturnsOkResponse() throws Exception {
        UUID photoId = UUID.randomUUID();

        FaceDetectionResult detected = new FaceDetectionResult(
                List.of(
                        new FaceLandmark("left_eye", 0.30, 0.35),
                        new FaceLandmark("right_eye", 0.70, 0.35),
                        new FaceLandmark("nose", 0.50, 0.55),
                        new FaceLandmark("left_mouth", 0.35, 0.75),
                        new FaceLandmark("right_mouth", 0.65, 0.75)),
                new FaceBoundingBox(0.20, 0.20, 0.60, 0.60),
                0.5);

        when(detectFaceLandmarksUseCase.execute(photoId)).thenReturn(detected);

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.result.landmarks").isArray())
                .andExpect(jsonPath("$.result.landmarks.length()").value(5))
                .andExpect(jsonPath("$.result.landmarks[0].name").value("left_eye"))
                .andExpect(jsonPath("$.result.boundingBox.xMin").value(0.2))
                .andExpect(jsonPath("$.result.boundingBox.yMin").value(0.2))
                .andExpect(jsonPath("$.result.boundingBox.width").value(0.6))
                .andExpect(jsonPath("$.result.boundingBox.height").value(0.6))
                .andExpect(jsonPath("$.result.confidence").value(0.5));
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は photoId 不明の場合 404 + problem+json")
    void postFaceDetections_PhotoNotFound_ReturnsNotFoundProblemDetails() throws Exception {
        UUID photoId = UUID.randomUUID();
        when(detectFaceLandmarksUseCase.execute(photoId)).thenThrow(new PhotoNotFoundException("photo not found"));

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Not Found"))
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は顔が検出できない場合 422 + problem+json")
    void postFaceDetections_FaceNotDetected_ReturnsUnprocessableEntityProblemDetails() throws Exception {
        UUID photoId = UUID.randomUUID();
        when(detectFaceLandmarksUseCase.execute(photoId)).thenThrow(new FaceNotDetectedException("face not detected"));

        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", photoId))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Unprocessable Entity"))
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.errors[0].field").value("face"));
    }

    @Test
    @DisplayName("POST /api/v1/photos/{photoId}/face-detections は不正UUIDの場合 400 + problem+json")
    void postFaceDetections_InvalidUuid_ReturnsBadRequestProblemDetails() throws Exception {
        mockMvc.perform(post("/api/v1/photos/{photoId}/face-detections", "not-a-uuid"))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/problem+json"))
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.errors[0].field").value("photoId"));
    }
}
