#!/usr/bin/env bash
# Proxy Gradle wrapper for the workspace root. This forwards any invocations
# to the actual Android project wrapper located in android_frontend/.
set -euo pipefail

# Navigate to the Android project root
cd "$(dirname "$0")/android_frontend"

# Execute the Android project's Gradle wrapper with all original arguments
exec ./gradlew "$@"
