package com.hatomask;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("HatomaskApplication 統合テスト")
class HatomaskApplicationTests extends AbstractIntegrationTest {

    @Test
    @DisplayName("アプリケーションコンテキストが正常にロードされる")
    void contextLoads() {
        // このテストはアプリケーションコンテキストが正常にロードされることを確認します
        // Testcontainersで起動したPostgreSQLコンテナに接続し、
        // Flywayマイグレーションも実行された状態でテストが行われます
    }
}
