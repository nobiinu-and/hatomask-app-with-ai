package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ImageDimensions 単体テスト")
class ImageDimensionsTest {

    @Test
    @DisplayName("of() は width=1,height=1 を受け取ると生成できる")
    void of_Valid_Creates() {
        ImageDimensions dimensions = ImageDimensions.of(1, 1);
        assertThat(dimensions.width()).isEqualTo(1);
        assertThat(dimensions.height()).isEqualTo(1);
    }

    @Test
    @DisplayName("of() は width=0 を受け取ると例外が発生する")
    void of_WidthZero_Throws() {
        assertThatThrownBy(() -> ImageDimensions.of(0, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("of() は height=0 を受け取ると例外が発生する")
    void of_HeightZero_Throws() {
        assertThatThrownBy(() -> ImageDimensions.of(1, 0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("of() は負の値を受け取ると例外が発生する")
    void of_Negative_Throws() {
        assertThatThrownBy(() -> ImageDimensions.of(-1, 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
