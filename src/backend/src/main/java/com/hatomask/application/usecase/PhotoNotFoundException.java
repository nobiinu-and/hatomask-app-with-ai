package com.hatomask.application.usecase;

/**
 * 写真が見つからない場合の例外.
 */
public class PhotoNotFoundException extends RuntimeException {

    /**
     * コンストラクタ.
     *
     * @param message エラーメッセージ
     */
    public PhotoNotFoundException(String message) {
        super(message);
    }
}
