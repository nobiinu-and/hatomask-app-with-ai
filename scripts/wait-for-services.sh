#!/bin/bash
set -e

FRONTEND_URL="${FRONTEND_URL:-http://localhost:3000}"
BACKEND_URL="${BACKEND_URL:-http://localhost:8080/api/v1/hello}"
TIMEOUT="${TIMEOUT:-60}"

echo "Waiting for services to be ready..."
echo "Frontend URL: $FRONTEND_URL"
echo "Backend URL: $BACKEND_URL"
echo "Timeout: ${TIMEOUT}s"

wait_for_service() {
    local url=$1
    local service_name=$2
    local start_time=$(date +%s)
    
    echo "Waiting for $service_name..."
    
    while true; do
        if curl -f -s -o /dev/null "$url"; then
            echo "✓ $service_name is ready!"
            return 0
        fi
        
        local current_time=$(date +%s)
        local elapsed=$((current_time - start_time))
        
        if [ $elapsed -ge $TIMEOUT ]; then
            echo "✗ Timeout waiting for $service_name after ${TIMEOUT}s"
            return 1
        fi
        
        echo "  Waiting for $service_name... (${elapsed}s elapsed)"
        sleep 2
    done
}

# バックエンドの起動を待機
wait_for_service "$BACKEND_URL" "Backend"

# フロントエンドの起動を待機
wait_for_service "$FRONTEND_URL" "Frontend"

echo ""
echo "All services are ready!"
