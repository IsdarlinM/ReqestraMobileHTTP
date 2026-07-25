@echo off
setlocal
cd /d "%~dp0\.."

call gradlew.bat --version
if errorlevel 1 exit /b %errorlevel%

call gradlew.bat clean testDebugUnitTest lintDebug assembleDebug
if errorlevel 1 exit /b %errorlevel%

echo [OK] Unit tests, Android lint and debug APK build completed.
echo APK: app\build\outputs\apk\debug\app-debug.apk
endlocal
