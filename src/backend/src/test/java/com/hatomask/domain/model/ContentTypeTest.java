package com.hatomask.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ContentTypeTest {

    @Nested
    class 有効なMIMEタイプを受け取るとContentTypeが生成される {

        @Test
        void imageJpegを受け取ると正常に生成される() {
            ContentType contentType = new ContentType("image/jpeg");
            assertEquals("image/jpeg", contentType.getValue());
        }

        @Test
        void imagePngを受け取ると正常に生成される() {
            ContentType contentType = new ContentType("image/png");
            assertEquals("image/png", contentType.getValue());
        }
    }

    @Nested
    class バリデーションエラーでContentType生成が失敗する {

        @Test
        void imageGifを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ContentType("image/gif");
            });
        }

        @Test
        void textPlainを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ContentType("text/plain");
            });
        }

        @Test
        void 空文字列を受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ContentType("");
            });
        }

        @Test
        void nullを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new ContentType(null);
            });
        }
    }

    @Nested
    class 値の取得と比較 {

        @Test
        void getValueは設定したMIMEタイプを返す() {
            ContentType contentType = new ContentType("image/jpeg");
            assertEquals("image/jpeg", contentType.getValue());
        }

        @Test
        void 同じ値のContentTypeは等価である() {
            ContentType contentType1 = new ContentType("image/jpeg");
            ContentType contentType2 = new ContentType("image/jpeg");
            assertEquals(contentType1, contentType2);
            assertEquals(contentType1.hashCode(), contentType2.hashCode());
        }

        @Test
        void 異なる値のContentTypeは等価でない() {
            ContentType contentType1 = new ContentType("image/jpeg");
            ContentType contentType2 = new ContentType("image/png");
            assertNotEquals(contentType1, contentType2);
        }
    }
}
