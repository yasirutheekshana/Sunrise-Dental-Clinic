@echo off
echo ========================================
echo  Starting Sunrise Dental Clinic Server
echo ========================================

cd /d "%~dp0"

if not exist bin\com\sunrisedental\Main.class (
    echo Binaries not found. Running build.bat first...
    call build.bat
)

echo Starting Java HTTP Server on http://localhost:8080 ...
java -cp "bin;lib/*" com.sunrisedental.Main
