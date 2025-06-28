#!/bin/bash

echo "========================================"
echo "    TeleportPlugin Build Script"
echo "========================================"
echo

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "ERROR: Maven is not installed or not in PATH"
    echo
    echo "Please install Maven first:"
    echo "Ubuntu/Debian: sudo apt install maven"
    echo "CentOS/RHEL: sudo yum install maven"
    echo "macOS: brew install maven"
    echo "Or download from: https://maven.apache.org/download.cgi"
    echo
    exit 1
fi

echo "Maven found! Building plugin..."
echo

# Build the plugin
mvn clean package

if [ $? -eq 0 ]; then
    echo
    echo "========================================"
    echo "    BUILD SUCCESSFUL!"
    echo "========================================"
    echo
    echo "Plugin JAR location: target/teleportplugin-1.0-SNAPSHOT.jar"
    echo
    echo "Installation steps:"
    echo "1. Copy the JAR file to your server's plugins/ folder"
    echo "2. Restart your Minecraft server"
    echo "3. Enjoy the plugin!"
    echo
else
    echo
    echo "========================================"
    echo "    BUILD FAILED!"
    echo "========================================"
    echo
    echo "Please check the error messages above."
    echo "Make sure you have Java 17+ installed."
    echo
fi
