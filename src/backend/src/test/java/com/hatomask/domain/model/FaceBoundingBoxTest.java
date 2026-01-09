package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FaceBoundingBox 単体テスト")
class FaceBoundingBoxTest {

    @Test
    @DisplayName("コンストラクタは有効な値を受け取ると生成できる")
    void constructor_Valid_CreatesInstance() {
        FaceBoundingBox bb = new FaceBoundingBox(0.2, 0.3, 0.6, 0.5);

        assertThat(bb.xMin()).isEqualTo(0.2);
        assertThat(bb.yMin()).isEqualTo(0.3);
        assertThat(bb.width()).isEqualTo(0.6);
        assertThat(bb.height()).isEqualTo(0.5);
    }

    @Test
    @DisplayName("コンストラクタは width/height が 0 以下の場合 IllegalArgumentException")
    void constructor_WidthOrHeightZero_Throws() {
        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 0.0, 0.0, 1.0))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 0.0, 1.0, 0.0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは xMin/yMin が範囲外の場合 IllegalArgumentException")
    void constructor_XMinYMinOutOfRange_Throws() {
        assertThatThrownBy(() -> new FaceBoundingBox(-0.0001, 0.0, 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 1.0001, 0.5, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは width/height が範囲外の場合 IllegalArgumentException")
    void constructor_WidthHeightOutOfRange_Throws() {
        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 0.0, 1.0001, 0.5))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 0.0, 0.5, 1.0001))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは xMin + width が 1.0 を超える場合 IllegalArgumentException")
    void constructor_XMinPlusWidthTooLarge_Throws() {
        assertThatThrownBy(() -> new FaceBoundingBox(0.8, 0.0, 0.21, 0.5))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("コンストラクタは yMin + height が 1.0 を超える場合 IllegalArgumentException")
    void constructor_YMinPlusHeightTooLarge_Throws() {
        assertThatThrownBy(() -> new FaceBoundingBox(0.0, 0.8, 0.5, 0.21))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
