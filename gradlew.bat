@echo off
REM Delegate Gradle wrapper to the Android subproject wrapper.
set SCRIPT_DIR=%~dp0
call "%SCRIPT_DIR%android_frontend\gradlew.bat" %*
