@echo off
setlocal
set "APP_HOME=%~dp0"
set "WRAPPER_DIR=%APP_HOME%gradle\wrapper"
set "WRAPPER_JAR=%WRAPPER_DIR%\gradle-wrapper.jar"

java "%WRAPPER_DIR%\WrapperBootstrap.java" "%WRAPPER_DIR%"
if errorlevel 1 exit /b %errorlevel%

java -classpath "%WRAPPER_JAR%" org.gradle.wrapper.GradleWrapperMain %*
endlocal
