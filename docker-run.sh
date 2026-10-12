#!/usr/bin/env bash
set -euo pipefail

echo ""
echo " =========================================="
echo "  Quarkus DDD | Docker Mode"
echo " =========================================="
echo ""

# ── Verificar .env ─────────────────────────────────────────────────────────
if [ ! -f ".env" ]; then
    echo " [AVISO] Arquivo .env nao encontrado."
    echo "         Copiando .env.example para .env..."
    cp .env.example .env
    echo "         Edite o .env antes de continuar."
    exit 1
fi

# ── Argumento: down / reset / logs ─────────────────────────────────────────
case "${1:-}" in
    down)
        echo "[*] Parando containers..."
        docker compose down
        exit 0
        ;;
    reset)
        echo "[*] Reset completo (remove volumes)..."
        docker compose down -v
        exit 0
        ;;
    logs)
        docker compose logs -f app
        exit 0
        ;;
esac

# ── Subir stack ────────────────────────────────────────────────────────────
echo "[1/1] Subindo stack completa com rebuild..."
echo "      App:        http://localhost:8081"
echo "      Swagger UI: http://localhost:8081/q/swagger-ui"
echo "      Keycloak:   http://localhost:8080"
echo "      Vault UI:   http://localhost:8200"
echo ""
docker compose up --build "$@"
