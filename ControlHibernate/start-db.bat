@echo off
echo ========================================
echo   LEVANTAR BASE DE DATOS DOCKER
echo ========================================
echo.

echo [1/4] Verificando Docker...
docker --version >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no esta instalado o no esta en el PATH
    echo.
    echo Por favor instala Docker Desktop desde:
    echo https://www.docker.com/products/docker-desktop
    pause
    exit /b 1
)
echo [OK] Docker instalado
echo.

echo [2/4] Verificando si Docker esta corriendo...
docker info >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Docker no esta corriendo
    echo.
    echo Por favor inicia Docker Desktop y vuelve a ejecutar este script
    pause
    exit /b 1
)
echo [OK] Docker corriendo
echo.

echo [3/4] Levantando contenedor MySQL...
docker-compose up -d
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Fallo al levantar el contenedor
    pause
    exit /b 1
)
echo [OK] Contenedor levantado
echo.

echo [4/4] Esperando que MySQL este listo...
timeout /t 10 /nobreak >nul
echo [OK] MySQL deberia estar listo
echo.

echo ========================================
echo   BASE DE DATOS LISTA
echo ========================================
echo.
echo Conexion:
echo   Host: localhost
echo   Puerto: 3306
echo   Base de datos: control_presencia
echo   Usuario: appuser
echo   Password: AppPass123!
echo.
echo Usuario root:
echo   Usuario: root
echo   Password: RootPass123!
echo.
echo Contenedor: control_presencia_db
echo.
echo Comandos utiles:
echo   Ver logs: docker logs control_presencia_db
echo   Parar: docker-compose down
echo   Reiniciar: docker-compose restart
echo.
echo ========================================
echo.
echo Ahora puedes ejecutar la aplicacion!
echo.
pause

