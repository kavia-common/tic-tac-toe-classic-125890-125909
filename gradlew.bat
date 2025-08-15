@echo off
REM Proxy Gradle wrapper for the workspace root. Forwards to android_frontend\gradlew.bat
setlocal
set SCRIPT_DIR=%~dp0
pushd "%SCRIPT_DIR%android_frontend"
call gradlew.bat %*
set EXIT_CODE=%ERRORLEVEL%
popd
exit /b %EXIT_CODE%
