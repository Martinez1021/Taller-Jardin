@echo off
chcp 65001 >nul
cls
echo.
echo ╔════════════════════════════════════════════════════════════╗
echo ║  🔧 TALLER DE REPARACIÓN - Ejecutar App                    ║
echo ╚════════════════════════════════════════════════════════════╝
echo.

REM Verificar si Docker está corriendo
echo [1/2] Verificando servicios Docker...
docker ps >nul 2>&1
if %errorlevel% neq 0 (
    echo ⚠️  Docker no está corriendo. Iniciando servicios...
    docker-compose up -d
    echo ⏳ Esperando a que MongoDB esté listo...
    timeout /t 10 /nobreak >nul
) else (
    echo ✓ Docker OK
)

echo.
echo [2/2] Ejecutando aplicación JavaFX...
echo.
call .\mvnw javafx:run
pause
