@echo off
setlocal EnableDelayedExpansion

title Quarkus Docker Runner

echo.
echo  ==========================================
echo   Quarkus DDD ^| Docker Mode
echo  ==========================================
echo.

:: ── Verificar .env ─────────────────────────────────────────────────────────
if not exist ".env" (
    echo  [AVISO] Arquivo .env nao encontrado.
    echo          Copiando .env.example para .env...
    copy .env.example .env >nul
    echo          Edite o .env antes de continuar.
    pause
    exit /b 1
)

:: ── Argumento: down / reset / logs ─────────────────────────────────────────
if "%1"=="down" (
    echo [*] Parando containers...
    docker compose down
    goto :end
)
if "%1"=="reset" (
    echo [*] Reset completo ^(remove volumes^)...
    docker compose down -v
    goto :end
)
if "%1"=="logs" (
    docker compose logs -f app
    goto :end
)

:: ── Subir stack ────────────────────────────────────────────────────────────
echo [1/1] Subindo stack completa com rebuild...
echo       App:        http://localhost:8081
echo       Swagger UI: http://localhost:8081/q/swagger-ui
echo       Keycloak:   http://localhost:8080
echo       Vault UI:   http://localhost:8200
echo.
docker compose up --build %*

:end
endlocal
