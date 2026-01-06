#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage:
  scripts/ai-chatlog-export.sh [INPUT] [options]

Args:
  INPUT                 Input JSON log file (default: docs/ai/logs/chat.json)

Options:
  --out DIR             Output base directory (default: docs/ai/logs)
  --tag TAG             Output subdir tag (e.g. 2026-01-06, 01_photo_upload)
  --date                Use today's date (YYYY-MM-DD) as a subdir tag
  --spec SPEC           Use SPEC as a subdir tag (e.g. 01_photo_upload)
  -h, --help             Show this help

Examples:
  # Save under docs/ai/logs/2026-01-06/
  scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --date

  # Save under docs/ai/logs/01_photo_upload/
  scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --spec 01_photo_upload

  # Save under docs/ai/logs/2026-01-06/01_photo_upload/
  scripts/ai-chatlog-export.sh docs/ai/logs/chat.json --date --spec 01_photo_upload
EOF
}

INPUT="docs/ai/logs/chat.json"
OUT_BASE_DIR="docs/ai/logs"
TAG=""
TAG_PARTS=()

if [[ ${1:-} != "" && ${1:-} != -* ]]; then
  INPUT="$1"
  shift
fi

while [[ $# -gt 0 ]]; do
  case "$1" in
    --out)
      OUT_BASE_DIR="$2"
      shift 2
      ;;
    --tag)
      TAG="$2"
      shift 2
      ;;
    --date)
      TAG_PARTS+=("$(date +%F)")
      shift
      ;;
    --spec)
      TAG_PARTS+=("$2")
      shift 2
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown option: $1" >&2
      usage >&2
      exit 2
      ;;
  esac
done

if [[ -n "$TAG" ]]; then
  TAG_PARTS+=("$TAG")
fi

join_by_slash() {
  local IFS="/"
  echo "$*"
}

OUT_DIR="$OUT_BASE_DIR"
if [[ ${#TAG_PARTS[@]} -gt 0 ]]; then
  OUT_DIR="$OUT_BASE_DIR/$(join_by_slash "${TAG_PARTS[@]}")"
fi

if [[ ! -f "$INPUT" ]]; then
  echo "Input not found: $INPUT" >&2
  exit 1
fi

mkdir -p "$OUT_DIR"

COMPACT_JSON="$OUT_DIR/chat.compact.json"
COMPACT_MD="$OUT_DIR/chat.compact.md"
STATS_JSON="$OUT_DIR/chat.stats.json"

# 1) 読み物用（本文中心）
jq '{
  responderUsername,
  requests: [
    .requests[] | {
      requestId,
      user: (.message.text // ""),
      assistant: (
        .response
        | map(select(.kind? != "thinking"))
        | map(.value? // "")
        | join("")
      )
    }
  ]
}' "$INPUT" > "$COMPACT_JSON"

# 2) 読み物用Markdown
jq -r '.requests[]
  | "## \(.requestId)\n\n### User\n\n\(.user | gsub("\\r\\n";"\\n"))\n\n### Assistant\n\n\(.assistant | gsub("\\r\\n";"\\n"))\n"
' "$COMPACT_JSON" > "$COMPACT_MD"

# 3) 分析用（ツール呼び出し回数など）
jq '{
  responderUsername,
  totalRequests: (.requests | length),
  totalThinkingItems: ([.requests[].response[]? | select(.kind? == "thinking")] | length),
  totalPrepareToolInvocations: ([.requests[].response[]? | select(.kind? == "prepareToolInvocation")] | length),
  toolCallsByName: (
    [ .requests[].response[]?
      | select(.kind? == "prepareToolInvocation")
      | .toolName
    ]
    | sort
    | group_by(.)
    | map({ tool: .[0], count: length })
  )
}' "$INPUT" > "$STATS_JSON"

echo "Wrote: $COMPACT_JSON"
echo "Wrote: $COMPACT_MD"
echo "Wrote: $STATS_JSON"
