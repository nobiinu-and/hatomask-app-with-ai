package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MimeType 単体テスト")
class MimeTypeTest {

    @Test
    @DisplayName("fromValue() は image/jpeg を MimeType に変換できる")
    void fromValue_ImageJpeg_ReturnsMimeType() {
        assertThat(MimeType.fromValue("image/jpeg")).isEqualTo(MimeType.IMAGE_JPEG);
    }

    @Test
    @DisplayName("fromValue() は image/png を MimeType に変換できる")
    void fromValue_ImagePng_ReturnsMimeType() {
        assertThat(MimeType.fromValue("image/png")).isEqualTo(MimeType.IMAGE_PNG);
    }

    @Test
    @DisplayName("fromValue() は未対応の値を受け取ると例外が発生する")
    void fromValue_Unsupported_Throws() {
        assertThatThrownBy(() -> MimeType.fromValue("image/gif"))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("fromValue() は空文字列を受け取ると例外が発生する")
    void fromValue_Empty_Throws() {
        assertThatThrownBy(() -> MimeType.fromValue(""))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("fromValue() はnullを受け取ると例外が発生する")
    void fromValue_Null_Throws() {
        assertThatThrownBy(() -> MimeType.fromValue(null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
