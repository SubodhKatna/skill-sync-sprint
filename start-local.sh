#!/usr/bin/env bash

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

mvn clean package -DskipTests
docker compose up -d













cat <<'EOF'
Skill Sync backend is starting.

Discovery: http://localhost:8761
Gateway: http://localhost:8080
Zipkin: http://localhost:9411
Prometheus: http://localhost:9090
Grafana: http://localhost:3000 (admin/admin)
Swagger examples:
  http://localhost:8081/swagger-ui.html
  http://localhost:8082/swagger-ui.html
  http://localhost:8083/swagger-ui.html
EOF
