#!/usr/bin/env bash
set -euo pipefail

echo ""
echo " =========================================="
echo "  Quarkus DDD | Dev Mode"
echo " =========================================="
echo ""

# ── 1. Matar processos Java que travam o build ─────────────────────────────
echo "[1/3] Encerrando processos Java em execucao..."
if pkill -f "quarkus-ddd" 2>/dev/null; then
    echo "      Processos encerrados."
else
    echo "      Nenhum processo encontrado."
fi
sleep 1

# ── 2. Recriar secrets no Vault local ───────────────────────────────────────
echo ""
echo "[2/4] Verificando Vault e recriando secrets..."
if docker exec -e VAULT_ADDR=http://127.0.0.1:8200 -e VAULT_TOKEN=myroot vault vault kv put secret/olist-app \
    db.username=olist db.password=olist db.port=5432 db.name=olist \
    keycloak.client-secret=spring-webflux-secret \
    jwt.secret=supersecretjwtsignkey1234567890ab > /dev/null 2>&1; then
    echo "      Secrets gravados no Vault."
else
    echo "      [AVISO] Vault nao esta rodando. Inicie com: docker compose up vault -d"
fi

# ── 3. Clean ───────────────────────────────────────────────────────────────
echo ""
echo "[3/4] Executando clean..."
./gradlew clean

# ── 4. quarkusDev ─────────────────────────────────────────────────────────
echo ""
echo "[4/4] Iniciando quarkusDev em http://localhost:8081 ..."
echo "      Swagger UI: http://localhost:8081/q/swagger-ui"
echo ""
./gradlew quarkusDev
