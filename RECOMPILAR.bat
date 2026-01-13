@echo off
chcp 65001 >nul
cls
echo 🧹 Limpiando y recompilando el proyecto...
echo.
call .\mvnw clean compile
if %errorlevel% neq 0 (
    echo ❌ Error en la compilación.
    pause
    exit /b %errorlevel%
)

echo.
echo 🚀 Iniciando aplicación...
call .\mvnw javafx:run
pause