package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FaceDetectionResult 単体テスト")
class FaceDetectionResultTest {

    @Test
    @DisplayName("コンストラクタは 5 点の landmarks を受け取ると生成できる")
    void constructor_5Landmarks_CreatesInstance() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.2, 0.6, 0.6);
        List<FaceLandmark> landmarks = List.of(
                new FaceLandmark("left_eye", 0.3, 0.35),
                new FaceLandmark("right_eye", 0.7, 0.35),
                new FaceLandmark("nose", 0.5, 0.55),
                new FaceLandmark("left_mouth", 0.35, 0.75),
                new FaceLandmark("right_mouth", 0.65, 0.75));

        FaceDetectionResult result = new FaceDetectionResult(landmarks, bb, 0.5);

        assertThat(result.landmarks()).hasSize(5);
        assertThat(result.boundingBox()).isEqualTo(bb);
        assertThat(result.confidence()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("コンストラクタは 68 点の landmarks を受け取ると生成できる")
    void constructor_68Landmarks_CreatesInstance() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.2, 0.6, 0.6);
        List<FaceLandmark> landmarks = IntStream.range(0, 68)
                .mapToObj(i -> new FaceLandmark("point_" + i, 0.5, 0.5))
                .toList();

        FaceDetectionResult result = new FaceDetectionResult(landmarks, bb, 0.5);

        assertThat(result.landmarks()).hasSize(68);
        assertThat(result.boundingBox()).isEqualTo(bb);
        assertThat(result.confidence()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("コンストラクタは landmarks が null/空の場合 IllegalArgumentException")
    void constructor_LandmarksNullOrEmpty_Throws() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.2, 0.6, 0.6);

        assertThatThrownBy(() -> new FaceDetectionResult(null, bb, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceDetectionResult(List.of(), bb, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは landmarks の点数が 5/68 以外の場合 IllegalArgumentException")
    void constructor_LandmarksCountInvalid_Throws() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.2, 0.6, 0.6);
        List<FaceLandmark> four = IntStream.range(0, 4)
                .mapToObj(i -> new FaceLandmark("p" + i, 0.5, 0.5))
                .toList();
        List<FaceLandmark> sixtyNine = IntStream.range(0, 69)
                .mapToObj(i -> new FaceLandmark("p" + i, 0.5, 0.5))
                .toList();

        assertThatThrownBy(() -> new FaceDetectionResult(four, bb, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceDetectionResult(sixtyNine, bb, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは boundingBox が null の場合 IllegalArgumentException")
    void constructor_BoundingBoxNull_Throws() {
        List<FaceLandmark> landmarks = List.of(new FaceLandmark("nose", 0.5, 0.5),
                new FaceLandmark("left_eye", 0.3, 0.35),
                new FaceLandmark("right_eye", 0.7, 0.35),
                new FaceLandmark("left_mouth", 0.35, 0.75),
                new FaceLandmark("right_mouth", 0.65, 0.75));

        assertThatThrownBy(() -> new FaceDetectionResult(landmarks, null, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは confidence が範囲外の場合 IllegalArgumentException")
    void constructor_ConfidenceOutOfRange_Throws() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.2, 0.6, 0.6);
        List<FaceLandmark> landmarks = List.of(new FaceLandmark("nose", 0.5, 0.5),
                new FaceLandmark("left_eye", 0.3, 0.35),
                new FaceLandmark("right_eye", 0.7, 0.35),
                new FaceLandmark("left_mouth", 0.35, 0.75),
                new FaceLandmark("right_mouth", 0.65, 0.75));

        assertThatThrownBy(() -> new FaceDetectionResult(landmarks, bb, -0.0001))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceDetectionResult(landmarks, bb, 1.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
