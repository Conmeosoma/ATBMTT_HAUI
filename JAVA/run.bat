@echo off
REM ============================================
REM Schnorr Digital Signature Simulator
REM Build and Run Script for Windows
REM ============================================

echo.
echo ========================================
echo Schnorr Digital Signature Simulator
echo Compile and Run Script
echo ========================================
echo.

REM Check if Java is installed
java -version >nul 2>&1
if errorlevel 1 (
    echo ERROR: Java is not installed or not in PATH!
    echo Please install Java Development Kit (JDK) and try again.
    pause
    exit /b 1
)

REM Get the directory of this script
cd /d "%~dp0"

echo [1/5] Compiling SchorrSignatureParams.java...
javac SchorrSignatureParams.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrSignatureParams.java
    pause
    exit /b 1
)
echo [OK] SchorrSignatureParams.java compiled successfully

echo.
echo [2/5] Compiling SchorrKeyPair.java...
javac SchorrKeyPair.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrKeyPair.java
    pause
    exit /b 1
)
echo [OK] SchorrKeyPair.java compiled successfully

echo.
echo [3/5] Compiling SchorrSignature.java...
javac SchorrSignature.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrSignature.java
    pause
    exit /b 1
)
echo [OK] SchorrSignature.java compiled successfully

echo.
echo [4/5] Compiling SchorrSignatureAlgorithm.java...
javac SchorrSignatureAlgorithm.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrSignatureAlgorithm.java
    pause
    exit /b 1
)
echo [OK] SchorrSignatureAlgorithm.java compiled successfully

echo.
echo [5/5] Compiling SchorrGUI.java...
javac SchorrGUI.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrGUI.java
    pause
    exit /b 1
)
echo [OK] SchorrGUI.java compiled successfully

echo.
echo [6/6] Compiling SchorrMain.java...
javac SchorrMain.java
if errorlevel 1 (
    echo ERROR: Failed to compile SchorrMain.java
    pause
    exit /b 1
)
echo [OK] SchorrMain.java compiled successfully

echo.
echo ========================================
echo Compilation completed successfully!
echo ========================================
echo.
echo Starting Schnorr Digital Signature Simulator...
echo.

REM Run the application
java SchorrMain

if errorlevel 1 (
    echo ERROR: Failed to run SchorrGUI
    pause
    exit /b 1
)

pause
