@echo off
echo ========================================
echo   PARAR BASE DE DATOS DOCKER
echo ========================================
echo.

echo Parando contenedor MySQL...
docker-compose down
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al parar el contenedor
    pause
    exit /b 1
)

echo [OK] Contenedor parado
echo.
echo Los datos estan guardados en un volumen Docker
echo y estaran disponibles cuando vuelvas a levantar el contenedor
echo.
pause

