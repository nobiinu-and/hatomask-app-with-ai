#!/bin/bash
set -e

echo "=========================================="
echo "OpenCV Setup for Codespace"
echo "=========================================="

# OpenCVと依存関係のインストール
echo "📦 Installing OpenCV and dependencies..."
sudo apt-get update
sudo apt-get install -y --no-install-recommends \
    python3-opencv \
    libopencv-dev \
    opencv-data \
    libgl1 \
    libglib2.0-0 \
    libsm6 \
    libxext6 \
    libxrender1 \
    libice6 \
    libgomp1 \
    ca-certificates \
    curl

# libgtk2.0のインストール (Ubuntu 24.04対応)
echo "📦 Installing GTK dependencies..."
sudo apt-get install -y --no-install-recommends libgtk2.0-0 \
    || sudo apt-get install -y --no-install-recommends libgtk2.0-0t64

# クリーンアップ
echo "🧹 Cleaning up..."
sudo rm -rf /var/lib/apt/lists/*

# LBFモデルのダウンロード
echo "📥 Downloading LBF model..."
sudo mkdir -p /opt/hatomask/models
sudo curl -fsSL -o /opt/hatomask/models/lbfmodel.yaml \
    https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml

# インストール確認
echo "✅ Verifying installation..."
if python3 -c "import cv2; print(f'OpenCV version: {cv2.__version__}')" 2>/dev/null; then
    echo "✅ OpenCV successfully installed!"
else
    echo "❌ OpenCV installation verification failed"
    exit 1
fi

# Haar Cascadeの確認
if [ -f "/usr/share/opencv4/haarcascades/haarcascade_frontalface_default.xml" ]; then
    echo "✅ Haar Cascade found"
else
    echo "⚠️  Haar Cascade not found at expected location"
fi

# LBFモデルの確認
if [ -f "/opt/hatomask/models/lbfmodel.yaml" ]; then
    echo "✅ LBF model downloaded"
else
    echo "❌ LBF model download failed"
    exit 1
fi

echo "=========================================="
echo "✨ OpenCV setup completed successfully!"
echo "=========================================="
