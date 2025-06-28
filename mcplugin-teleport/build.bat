@echo off
echo ========================================
echo    TeleportPlugin Build Script
echo ========================================
echo.

REM Check if Maven is installed
mvn --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Maven is not installed or not in PATH
    echo.
    echo Please install Maven first:
    echo 1. Download from: https://maven.apache.org/download.cgi
    echo 2. Extract and add to PATH
    echo 3. Or use an IDE like IntelliJ IDEA
    echo.
    pause
    exit /b 1
)

echo Maven found! Building plugin...
echo.

REM Build the plugin
mvn clean package

if %errorlevel% eq 0 (
    echo.
    echo ========================================
    echo    BUILD SUCCESSFUL!
    echo ========================================
    echo.
    echo Plugin JAR location: target\teleportplugin-1.0-SNAPSHOT.jar
    echo.
    echo Installation steps:
    echo 1. Copy the JAR file to your server's plugins/ folder
    echo 2. Restart your Minecraft server
    echo 3. Enjoy the plugin!
    echo.
) else (
    echo.
    echo ========================================
    echo    BUILD FAILED!
    echo ========================================
    echo.
    echo Please check the error messages above.
    echo Make sure you have Java 17+ installed.
    echo.
)

pause
