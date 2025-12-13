package com.hatomask.domain.model;

import java.util.Objects;

/**
 * コンテンツタイプを表すValueObject.
 * 画像のMIMEタイプ（image/jpeg, image/png）のみ許可する。
 */
public class ContentType {

    private static final String IMAGE_JPEG = "image/jpeg";
    private static final String IMAGE_PNG = "image/png";

    private final String value;

    /**
     * ContentTypeを生成する.
     *
     * @param value MIMEタイプ文字列
     * @throws IllegalArgumentException 許可されていないMIMEタイプ、null、または空文字列の場合
     */
    public ContentType(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("ContentType must not be null or empty");
        }
        if (!IMAGE_JPEG.equals(value) && !IMAGE_PNG.equals(value)) {
            throw new IllegalArgumentException("JPEG または PNG ファイルを選択してください");
        }
        this.value = value;
    }

    /**
     * MIMEタイプの文字列値を取得する.
     *
     * @return MIMEタイプ
     */
    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ContentType that = (ContentType) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ContentType{" + "value='" + value + '\'' + '}';
    }
}
