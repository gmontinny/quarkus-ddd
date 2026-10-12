@echo off
setlocal EnableDelayedExpansion

title Quarkus Dev Runner

echo.
echo  ==========================================
echo   Quarkus DDD ^| Dev Mode
echo  ==========================================
echo.

:: ── 1. Matar processos Java que travam o build ─────────────────────────────
echo [1/3] Encerrando processos Java em execucao...
taskkill /F /IM java.exe >nul 2>&1
if %errorlevel% == 0 (
    echo       Processos encerrados.
) else (
    echo       Nenhum processo Java encontrado.
)
timeout /t 1 /nobreak >nul

:: ── 2. Recriar secrets no Vault local ───────────────────────────────────────
echo.
echo [2/4] Verificando Vault e recriando secrets...
docker exec -e VAULT_ADDR=http://127.0.0.1:8200 -e VAULT_TOKEN=myroot vault vault kv put secret/olist-app db.username=olist db.password=olist db.port=5432 db.name=olist keycloak.client-secret=spring-webflux-secret jwt.secret=supersecretjwtsignkey1234567890ab >nul 2>&1
if %errorlevel% == 0 (
    echo       Secrets gravados no Vault.
) else (
    echo       [AVISO] Vault nao esta rodando. Inicie com: docker compose up vault -d
)

:: ── 3. Clean ───────────────────────────────────────────────────────────────
echo.
echo [3/4] Executando clean...
call gradlew.bat clean
if %errorlevel% neq 0 (
    echo.
    echo  [ERRO] Falha no clean. Abortando.
    pause
    exit /b 1
)

:: ── 4. quarkusDev ─────────────────────────────────────────────────────────
echo.
echo [4/4] Iniciando quarkusDev em http://localhost:8081 ...
echo       Swagger UI: http://localhost:8081/q/swagger-ui
echo.
call gradlew.bat quarkusDev
