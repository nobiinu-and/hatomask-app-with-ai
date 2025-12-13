-- 写真テーブル作成
CREATE TABLE photos (
    id UUID PRIMARY KEY,
    original_file_name VARCHAR(255) NOT NULL,
    content_type VARCHAR(50) NOT NULL,
    file_size BIGINT NOT NULL,
    image_data BYTEA NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- インデックス作成
CREATE INDEX idx_photos_created_at ON photos(created_at);
