package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FaceLandmark 単体テスト")
class FaceLandmarkTest {

    @Test
    @DisplayName("コンストラクタは有効な値を受け取ると生成できる")
    void constructor_Valid_CreatesInstance() {
        FaceLandmark lm = new FaceLandmark("left_eye", 0.0, 1.0);

        assertThat(lm.name()).isEqualTo("left_eye");
        assertThat(lm.x()).isEqualTo(0.0);
        assertThat(lm.y()).isEqualTo(1.0);
    }

    @Test
    @DisplayName("コンストラクタは name が空の場合 IllegalArgumentException")
    void constructor_NameBlank_Throws() {
        assertThatThrownBy(() -> new FaceLandmark("", 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceLandmark("  ", 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは name が null の場合 IllegalArgumentException")
    void constructor_NameNull_Throws() {
        assertThatThrownBy(() -> new FaceLandmark(null, 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは x が範囲外の場合 IllegalArgumentException")
    void constructor_XOutOfRange_Throws() {
        assertThatThrownBy(() -> new FaceLandmark("nose", -0.0001, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceLandmark("nose", 1.0001, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは y が範囲外の場合 IllegalArgumentException")
    void constructor_YOutOfRange_Throws() {
        assertThatThrownBy(() -> new FaceLandmark("nose", 0.5, -0.0001))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceLandmark("nose", 0.5, 1.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
