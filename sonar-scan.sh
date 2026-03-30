#!/bin/bash
# Run SonarQube analysis for the entire SkillSync backend.
# Usage: ./sonar-scan.sh <your-sonar-token>
#
# Prerequisites:
#   1. SonarQube is running: docker compose up sonarqube -d
#   2. You have created a project token at http://localhost:9000

set -e

TOKEN=${1:-$SONAR_TOKEN}

if [ -z "$TOKEN" ]; then
  echo "ERROR: Provide a SonarQube token as first argument or set SONAR_TOKEN env var."
  echo "Usage: ./sonar-scan.sh <token>"
  exit 1
fi

echo "==> Building project and generating JaCoCo coverage reports..."
mvn clean verify -DskipTests=false

echo "==> Running SonarQube analysis..."
mvn sonar:sonar \
  -Dsonar.token="$TOKEN" \
  -Dsonar.host.url=http://localhost:9000

echo ""
echo "==> Done! Open http://localhost:9000/dashboard?id=skillsync-backend to view results."
