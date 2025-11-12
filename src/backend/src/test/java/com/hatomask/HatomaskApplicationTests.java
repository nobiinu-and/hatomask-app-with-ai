package com.hatomask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("HatomaskApplication 統合テスト")
class HatomaskApplicationTests {

    @Test
    @DisplayName("アプリケーションコンテキストが正常にロードされる")
    void contextLoads() {
        // このテストはアプリケーションコンテキストが正常にロードされることを確認します
    }
}
