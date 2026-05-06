#!/bin/bash

# ============================================
# Schnorr Digital Signature Simulator
# Build and Run Script for Linux/Mac
# ============================================

echo
echo "========================================"
echo "Schnorr Digital Signature Simulator"
echo "Compile and Run Script"
echo "========================================"
echo

# Check if Java is installed
if ! command -v java &> /dev/null; then
    echo "ERROR: Java is not installed!"
    echo "Please install Java Development Kit (JDK) and try again."
    exit 1
fi

# Get the directory of this script
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
cd "$SCRIPT_DIR"

echo "[1/5] Compiling SchorrSignatureParams.java..."
javac SchorrSignatureParams.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrSignatureParams.java"
    exit 1
fi
echo "[OK] SchorrSignatureParams.java compiled successfully"

echo
echo "[2/5] Compiling SchorrKeyPair.java..."
javac SchorrKeyPair.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrKeyPair.java"
    exit 1
fi
echo "[OK] SchorrKeyPair.java compiled successfully"

echo
echo "[3/5] Compiling SchorrSignature.java..."
javac SchorrSignature.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrSignature.java"
    exit 1
fi
echo "[OK] SchorrSignature.java compiled successfully"

echo
echo "[4/5] Compiling SchorrSignatureAlgorithm.java..."
javac SchorrSignatureAlgorithm.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrSignatureAlgorithm.java"
    exit 1
fi
echo "[OK] SchorrSignatureAlgorithm.java compiled successfully"

echo
echo "[5/5] Compiling SchorrGUI.java..."
javac SchorrGUI.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrGUI.java"
    exit 1
fi
echo "[OK] SchorrGUI.java compiled successfully"

echo
echo "[6/6] Compiling SchorrMain.java..."
javac SchorrMain.java
if [ $? -ne 0 ]; then
    echo "ERROR: Failed to compile SchorrMain.java"
    exit 1
fi
echo "[OK] SchorrMain.java compiled successfully"

echo
echo "========================================"
echo "Compilation completed successfully!"
echo "========================================"
echo
echo "Starting Schnorr Digital Signature Simulator..."
echo

# Run the application
java SchorrMain

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to run SchorrGUI"
    exit 1
fi
