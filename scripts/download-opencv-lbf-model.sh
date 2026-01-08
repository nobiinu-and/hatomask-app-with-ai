#!/usr/bin/env bash
set -euo pipefail

# LBF facemark model downloader.
#
# This repository does NOT commit the model file because it is large.
# The model is required when running the backend with LBF_68 landmarks.

REPO_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
DEST_DIR_DEFAULT="$REPO_ROOT/dev/opencv-models"
DEST_FILE_DEFAULT="$DEST_DIR_DEFAULT/lbfmodel.yaml"

MODEL_URL_DEFAULT="https://raw.githubusercontent.com/kurnianggoro/GSOC2017/master/data/lbfmodel.yaml"
MODEL_SHA256_DEFAULT="70dd8b1657c42d1595d6bd13d97d932877b3bed54a95d3c4733a0f740d1fd66b"

DEST_FILE="${DEST_FILE:-$DEST_FILE_DEFAULT}"
MODEL_URL="${MODEL_URL:-$MODEL_URL_DEFAULT}"
MODEL_SHA256="${MODEL_SHA256:-$MODEL_SHA256_DEFAULT}"

mkdir -p "$(dirname "$DEST_FILE")"

tmpfile="$(mktemp)"
cleanup() {
  rm -f "$tmpfile"
}
trap cleanup EXIT

echo "Downloading LBF model..."
echo "  URL : $MODEL_URL"
echo "  Dest: $DEST_FILE"

curl -fsSL --retry 3 --retry-delay 2 -o "$tmpfile" "$MODEL_URL"

echo "Verifying sha256..."
downloaded_sha256="$(sha256sum "$tmpfile" | awk '{print $1}')"
if [[ "$downloaded_sha256" != "$MODEL_SHA256" ]]; then
  echo "ERROR: sha256 mismatch" >&2
  echo "  expected: $MODEL_SHA256" >&2
  echo "  got     : $downloaded_sha256" >&2
  exit 1
fi

mv -f "$tmpfile" "$DEST_FILE"
chmod 0644 "$DEST_FILE"

echo "OK: saved $DEST_FILE"