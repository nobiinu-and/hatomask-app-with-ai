package com.hatomask.domain.model;

import java.util.Objects;

/**
 * ファイルサイズを表すValueObject.
 * 1バイトから10MB（10,485,760バイト）までのサイズを許可する。
 */
public class FileSize {

    private static final long MIN_SIZE = 1L;
    private static final long MAX_SIZE = 10_485_760L; // 10MB

    private final Long value;

    /**
     * FileSizeを生成する.
     *
     * @param value ファイルサイズ（バイト）
     * @throws IllegalArgumentException nullまたは範囲外の値の場合
     */
    public FileSize(Long value) {
        if (value == null) {
            throw new IllegalArgumentException("FileSize must not be null");
        }
        if (value < MIN_SIZE || value > MAX_SIZE) {
            throw new IllegalArgumentException(
                    "FileSize must be between " + MIN_SIZE + " and " + MAX_SIZE + " bytes, but was: " + value);
        }
        this.value = value;
    }

    /**
     * ファイルサイズをバイト数で取得する.
     *
     * @return ファイルサイズ（バイト）
     */
    public Long getBytes() {
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
        FileSize fileSize = (FileSize) o;
        return Objects.equals(value, fileSize.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "FileSize{" + "value=" + value + '}';
    }
}
