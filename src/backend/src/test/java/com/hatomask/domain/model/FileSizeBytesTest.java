package com.hatomask.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FileSizeBytes 単体テスト")
class FileSizeBytesTest {

    @Test
    @DisplayName("of() は 1 バイトを受け取ると生成できる")
    void of_OneByte_Creates() {
        FileSizeBytes value = FileSizeBytes.of(1);
        assertThat(value.value()).isEqualTo(1);
    }

    @Test
    @DisplayName("of() は 10MB を受け取ると生成できる")
    void of_MaxBytes_Creates() {
        FileSizeBytes value = FileSizeBytes.of(FileSizeBytes.MAX_BYTES);
        assertThat(value.value()).isEqualTo(FileSizeBytes.MAX_BYTES);
    }

    @Test
    @DisplayName("of() は 0 バイトを受け取ると例外が発生する")
    void of_Zero_Throws() {
        assertThatThrownBy(() -> FileSizeBytes.of(0))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("of() は負の値を受け取ると例外が発生する")
    void of_Negative_Throws() {
        assertThatThrownBy(() -> FileSizeBytes.of(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("of() は 10MB+1 バイトを受け取ると例外が発生する")
    void of_TooLarge_Throws() {
        assertThatThrownBy(() -> FileSizeBytes.of(FileSizeBytes.MAX_BYTES + 1))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
