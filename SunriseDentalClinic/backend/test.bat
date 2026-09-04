@echo off
echo ========================================
echo  Running Sunrise Dental Clinic Unit Tests
echo ========================================

cd /d "%~dp0"

if not exist bin mkdir bin

echo Compiling test sources...
javac -encoding UTF-8 -cp "bin;lib/*" -d bin test\com\sunrisedental\*.java

if %ERRORLEVEL% neq 0 (
    echo Test compilation failed!
    exit /b %ERRORLEVEL%
)

echo.
echo Running JUnit Tests...
java -cp "bin;lib/*" org.junit.runner.JUnitCore com.sunrisedental.AuthServiceTest com.sunrisedental.AppointmentServiceTest com.sunrisedental.BillingServiceTest

if %ERRORLEVEL% equ 0 (
    echo.
    echo All tests PASSED!
) else (
    echo.
    echo Test failures detected!
    exit /b %ERRORLEVEL%
)
