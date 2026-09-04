@echo off
echo =========================================================
echo       Sunrise Dental Clinic Management System
echo =========================================================
echo.

cd /d "%~dp0backend"

if not exist bin\com\sunrisedental\Main.class (
    echo Building backend application...
    call build.bat
    if %ERRORLEVEL% neq 0 (
        echo Compilation failed! Exiting.
        pause
        exit /b %ERRORLEVEL%
    )
)

echo.
echo Launching Server...
call run.bat
