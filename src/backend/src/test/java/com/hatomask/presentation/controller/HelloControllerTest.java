package com.hatomask.presentation.controller;

import com.hatomask.presentation.dto.HelloResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HelloController 単体テスト")
class HelloControllerTest {

    private final HelloController controller = new HelloController();

    @Test
    @DisplayName("hello() は正常なレスポンスを返す")
    void hello_ReturnsValidResponse() {
        // When
        ResponseEntity<HelloResponse> response = controller.hello();

        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage())
                .isEqualTo("Hello, World from HatoMask Backend!");
    }

    @Test
    @DisplayName("hello() はnullではないボディを返す")
    void hello_ReturnsNonNullBody() {
        // When
        ResponseEntity<HelloResponse> response = controller.hello();

        // Then
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getMessage()).isNotBlank();
    }
}
