@echo off
echo ========================================
echo  Building Sunrise Dental Clinic Backend
echo ========================================

cd /d "%~dp0"

if not exist bin mkdir bin

echo Compiling Java source files...
javac -encoding UTF-8 -cp "lib/*" -d bin src\com\sunrisedental\model\*.java src\com\sunrisedental\dto\*.java src\com\sunrisedental\exception\*.java src\com\sunrisedental\util\*.java src\com\sunrisedental\session\*.java src\com\sunrisedental\repository\*.java src\com\sunrisedental\service\*.java src\com\sunrisedental\server\*.java src\com\sunrisedental\controller\*.java src\com\sunrisedental\Main.java

if %ERRORLEVEL% equ 0 (
    echo.
    echo Build SUCCESSFUL! Output classes compiled to 'backend/bin/'.
) else (
    echo.
    echo Build FAILED. Please check compiler errors above.
    exit /b %ERRORLEVEL%
)
