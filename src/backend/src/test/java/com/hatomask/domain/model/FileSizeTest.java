package com.hatomask.domain.model;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileSizeTest {

    @Nested
    class 有効なファイルサイズを受け取るとFileSizeが生成される {

        @Test
        void _1バイトを受け取ると正常に生成される() {
            FileSize fileSize = new FileSize(1L);
            assertEquals(1L, fileSize.getBytes());
        }

        @Test
        void _5MBを受け取ると正常に生成される() {
            FileSize fileSize = new FileSize(5_242_880L);
            assertEquals(5_242_880L, fileSize.getBytes());
        }

        @Test
        void _10MBを受け取ると正常に生成される() {
            FileSize fileSize = new FileSize(10_485_760L);
            assertEquals(10_485_760L, fileSize.getBytes());
        }
    }

    @Nested
    class バリデーションエラーでFileSize生成が失敗する {

        @Test
        void _0バイトを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new FileSize(0L);
            });
        }

        @Test
        void 負の値を受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new FileSize(-1L);
            });
        }

        @Test
        void _10MBプラス1バイトを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new FileSize(10_485_761L);
            });
        }

        @Test
        void nullを受け取るとIllegalArgumentExceptionが発生する() {
            assertThrows(IllegalArgumentException.class, () -> {
                new FileSize(null);
            });
        }
    }

    @Nested
    class 値の取得と比較 {

        @Test
        void getBytesは設定したバイト数を返す() {
            FileSize fileSize = new FileSize(5_242_880L);
            assertEquals(5_242_880L, fileSize.getBytes());
        }

        @Test
        void 同じ値のFileSizeは等価である() {
            FileSize fileSize1 = new FileSize(1024L);
            FileSize fileSize2 = new FileSize(1024L);
            assertEquals(fileSize1, fileSize2);
            assertEquals(fileSize1.hashCode(), fileSize2.hashCode());
        }

        @Test
        void 異なる値のFileSizeは等価でない() {
            FileSize fileSize1 = new FileSize(1024L);
            FileSize fileSize2 = new FileSize(2048L);
            assertNotEquals(fileSize1, fileSize2);
        }
    }
}
