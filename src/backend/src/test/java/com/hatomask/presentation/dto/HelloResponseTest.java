package com.hatomask.presentation.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HelloResponse 単体テスト")
class HelloResponseTest {

    @Test
    @DisplayName("デフォルトコンストラクタで生成できる")
    void canCreateWithDefaultConstructor() {
        // When
        HelloResponse response = new HelloResponse();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isNull();
    }

    @Test
    @DisplayName("引数付きコンストラクタで生成できる")
    void canCreateWithMessageConstructor() {
        // Given
        String message = "Test message";

        // When
        HelloResponse response = new HelloResponse(message);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("setterでメッセージを設定できる")
    void canSetMessage() {
        // Given
        HelloResponse response = new HelloResponse();
        String message = "Updated message";

        // When
        response.setMessage(message);

        // Then
        assertThat(response.getMessage()).isEqualTo(message);
    }

    @Test
    @DisplayName("equalsとhashCodeが正しく動作する")
    void equalsAndHashCodeWork() {
        // Given
        HelloResponse response1 = new HelloResponse("Test");
        HelloResponse response2 = new HelloResponse("Test");
        HelloResponse response3 = new HelloResponse("Different");

        // Then
        assertThat(response1).isEqualTo(response2);
        assertThat(response1).isNotEqualTo(response3);
        assertThat(response1.hashCode()).isEqualTo(response2.hashCode());
    }
}
