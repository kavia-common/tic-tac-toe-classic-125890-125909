#!/usr/bin/env bash
# Delegate Gradle wrapper to the Android subproject wrapper.
# This script allows CI that runs from the workspace root to execute Gradle tasks.
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
exec "${SCRIPT_DIR}/android_frontend/gradlew" "$@"
