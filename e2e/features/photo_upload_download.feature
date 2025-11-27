Feature: Photo upload and download (minimal)

  Scenario: JPEGファイルのアップロードとダウンロード
    Given ユーザーがHatoMaskアプリケーションにアクセスしている
    When ユーザーが「写真を選択」ボタンをクリックする
    And ユーザーがファイルサイズ5MBのJPEGファイルを選択する
    Then アップロードが成功する
    And プレビューエリアに選択した画像が表示される
    When ユーザーが「ダウンロード」ボタンをクリックする
    Then 元の画像がダウンロードされる
