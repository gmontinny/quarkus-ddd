# Quarkus DDD — Olist E-Commerce API

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Quarkus](https://img.shields.io/badge/Quarkus-3.40.1-blue?logo=quarkus)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14-blue?logo=postgresql)
![Keycloak](https://img.shields.io/badge/Keycloak-24.0.1-red?logo=keycloak)
![Vault](https://img.shields.io/badge/Vault-1.15-black?logo=vault)
![License](https://img.shields.io/badge/License-MIT-green)

API RESTful reativa construída com **Quarkus**, seguindo os princípios de **Domain-Driven Design (DDD)**, com segurança via **Keycloak + JWT**, gerenciamento de segredos com **HashiCorp Vault**, versionamento de banco com **Liquibase**, e documentação interativa via **Swagger UI**.

Os dados utilizados são do dataset público **Olist Brazilian E-Commerce** (Kaggle), importados automaticamente via Liquibase na inicialização.

---

## Índice

- [Visão Geral](#visão-geral)
- [Stack Tecnológica](#stack-tecnológica)
- [Arquitetura DDD](#arquitetura-ddd)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Pré-requisitos](#pré-requisitos)
- [Configuração e Execução](#configuração-e-execução)
- [Variáveis de Ambiente e Vault](#variáveis-de-ambiente-e-vault)
- [Segurança — Keycloak e JWT](#segurança--keycloak-e-jwt)
- [Endpoints da API](#endpoints-da-api)
- [HATEOAS — Nível 3 de Maturidade](#hateoas--nível-3-de-maturidade)
- [Rate Limiting](#rate-limiting)
- [Refresh Token](#refresh-token)
- [Liquibase — Versionamento do Banco](#liquibase--versionamento-do-banco)
- [Swagger UI](#swagger-ui)
- [Testes](#testes)
- [Docker e Docker Compose](#docker-e-docker-compose)
- [Boas Práticas Aplicadas](#boas-práticas-aplicadas)

---

## Visão Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                        Cliente (HTTP)                           │
└───────────────────────────┬─────────────────────────────────────┘
                            │
                    ┌───────▼────────┐
                    │  Rate Limiter  │  (100 req/min por IP)
                    └───────┬────────┘
                            │
                    ┌───────▼────────┐
                    │  JWT Filter    │  (Keycloak OIDC)
                    └───────┬────────┘
                            │
              ┌─────────────▼──────────────┐
              │     interfaces/rest         │  REST Resources (JAX-RS)
              │  AuthResource               │  HATEOAS Level 3
              │  CustomerResource           │
              │  OrderResource              │
              │  ProductResource            │
              │  SellerResource             │
              │  HealthResource             │
              └─────────────┬──────────────┘
                            │
              ┌─────────────▼──────────────┐
              │     application/service     │  Use Cases
              │  AuthService                │
              │  CustomerService            │
              │  OrderService               │
              │  ProductService             │
              │  SellerService              │
              └─────────────┬──────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        │                   │                   │
┌───────▼──────┐  ┌─────────▼──────┐  ┌────────▼───────┐
│ domain/model │  │domain/repository│  │domain/exception│
│  Entities    │  │  Interfaces     │  │  BusinessEx    │
└──────────────┘  └─────────┬──────┘  └────────────────┘
                            │
              ┌─────────────▼──────────────┐
              │  infrastructure/repository  │  Panache Reactive
              │  infrastructure/security    │  Keycloak Client
              │  infrastructure/ratelimit   │  ContainerRequestFilter
              │  infrastructure/config      │  OpenAPI, ExMapper
              └─────────────┬──────────────┘
                            │
              ┌─────────────▼──────────────┐
              │       PostgreSQL            │  Reactive PG Client
              │       Vault                 │  Secrets
              │       Keycloak              │  OIDC / JWT
              └────────────────────────────┘
```

---

## Stack Tecnológica

| Tecnologia | Versão | Função |
|---|---|---|
| Java | 21 | Linguagem principal |
| Quarkus | 3.40.1 | Framework principal |
| Hibernate Reactive + Panache | BOM 3.40.1 | ORM reativo |
| Hibernate Validator | BOM 3.40.1 | Bean Validation (Jakarta) |
| Reactive PostgreSQL Client | BOM 3.40.1 | Driver reativo |
| JDBC PostgreSQL | BOM 3.40.1 | Driver síncrono (Liquibase) |
| Liquibase | BOM 3.40.1 | Versionamento de banco |
| Keycloak | 24.0.1 | Identity Provider (OIDC) |
| SmallRye JWT | BOM 3.40.1 | Validação de tokens JWT |
| HashiCorp Vault | 1.15 | Gerenciamento de segredos |
| Quarkiverse Vault | 4.2.1 | Extensão Quarkus para Vault |
| SmallRye OpenAPI | BOM 3.40.1 | Documentação Swagger |
| SmallRye Fault Tolerance | BOM 3.40.1 | Resiliência |
| Quarkus Scheduler | BOM 3.40.1 | Agendamento de tarefas |
| PostgreSQL | 14 | Banco de dados |
| Docker / Compose | latest | Containerização |
| Gradle | 9.7.0 | Build tool |

---

## Arquitetura DDD

O projeto segue a arquitetura **Domain-Driven Design** com quatro camadas bem definidas:

### `domain/`
Núcleo do negócio. Não depende de nenhuma outra camada.
- **model/** — Entidades JPA (`Customer`, `Order`, `OrderItem`, `OrderItemId`, `OrderPayment`, `OrderPaymentId`, `OrderReview`, `Product`, `Seller`, `RefreshToken`)
- **repository/** — Interfaces de repositório (contratos)
- **exception/** — Exceções de domínio (`ResourceNotFoundException`, `BusinessException`)

### `application/`
Orquestra os casos de uso. Depende apenas do `domain/`.
- **service/** — Serviços de aplicação (`AuthService`, `CustomerService`, `OrderService`, `ProductService`, `SellerService`)
- **dto/request/** — Objetos de entrada validados com Bean Validation
- **dto/response/** — Objetos de saída com links HATEOAS (`PageResponse<T>`, `Link`, `TokenResponse`)
- **mapper/** — Conversão entre domínio e DTOs

### `infrastructure/`
Implementações técnicas. Depende do `domain/`.
- **repository/** — Implementações Panache Reactive dos repositórios
- **security/** — Cliente Keycloak para troca de tokens (`KeycloakAuthService`, `KeycloakTokenClient`, `KeycloakTokenResponse`)
- **ratelimit/** — Filtro JAX-RS de Rate Limiting (`RateLimitFilter`)
- **config/** — OpenAPI config, Exception Mapper global (`GlobalExceptionMapper`, `OpenApiConfig`)

### `interfaces/`
Ponto de entrada HTTP. Depende do `application/`.
- **rest/** — Resources JAX-RS com anotações OpenAPI e segurança por roles

---

## Estrutura do Projeto

```
quarkus-ddd/
├── data/                                    # CSVs do dataset Olist
│   ├── olist_customers_dataset.csv
│   ├── olist_orders_dataset.csv
│   ├── olist_order_items_dataset.csv
│   ├── olist_order_payments_dataset.csv
│   ├── olist_order_reviews_dataset.csv
│   ├── olist_products_dataset.csv
│   ├── olist_sellers_dataset.csv
│   ├── olist_geolocation_dataset.csv
│   └── product_category_name_translation.csv
├── docker/
│   └── init-db.sql
├── keycloak/
│   └── conf/
│       └── realm-export.json                # Realm pré-configurado
├── src/
│   ├── main/
│   │   ├── java/br/com/
│   │   │   ├── domain/
│   │   │   │   ├── model/                   # Entidades JPA
│   │   │   │   ├── repository/              # Interfaces de repositório
│   │   │   │   └── exception/               # Exceções de domínio
│   │   │   ├── application/
│   │   │   │   ├── service/                 # Casos de uso
│   │   │   │   ├── dto/
│   │   │   │   │   ├── request/             # DTOs de entrada
│   │   │   │   │   └── response/            # DTOs de saída + HATEOAS
│   │   │   │   └── mapper/                  # Conversores
│   │   │   ├── infrastructure/
│   │   │   │   ├── repository/              # Panache Reactive impls
│   │   │   │   ├── security/                # Keycloak client
│   │   │   │   ├── ratelimit/               # Rate limit filter
│   │   │   │   └── config/                  # OpenAPI, ExceptionMapper
│   │   │   ├── interfaces/
│   │   │   │   └── rest/                    # JAX-RS Resources
│   │   │   └── HealthResource.java          # Health check (br.com raiz)
│   │   └── resources/
│   │       ├── application.yml
│   │       └── db/changelog/
│   │           ├── db.changelog-master.xml
│   │           └── v1/
│   │               ├── 001-create-tables.xml
│   │               ├── 002-create-indexes.xml
│   │               └── 003-import-data.xml
│   └── test/
│       ├── java/br/com/
│       │   ├── interfaces/rest/
│       │   │   ├── AuthResourceTest.java
│       │   │   ├── CustomerResourceTest.java
│       │   │   └── ProductResourceTest.java
│       │   ├── application/service/
│       │   │   └── OrderServiceTest.java
│       │   └── ExampleResourceTest.java
│       └── resources/
│           └── application.yml
├── .env.example                             # Template de variáveis de ambiente
├── Dockerfile                               # Multi-stage build
├── docker-compose.yml                       # Orquestração completa
├── build.gradle
├── gradle.properties
└── settings.gradle
```

---

## Pré-requisitos

| Ferramenta | Versão mínima |
|---|---|
| Java JDK | 21+ |
| Docker | 24+ |
| Docker Compose | 2.20+ |
| Gradle | 9+ (ou usar `./gradlew`) |

---

## Configuração e Execução

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/quarkus-ddd.git
cd quarkus-ddd
```

### 2. Configurar variáveis de ambiente

```bash
cp .env.example .env
# Edite o .env com seus valores
```

### 3. Subir a infraestrutura completa (recomendado)

```bash
docker compose up --build
```

Ordem de inicialização garantida pelo `depends_on`:
1. `postgres` e `postgres-keycloak` — aguardam healthcheck
2. `keycloak` — aguarda `postgres-keycloak` healthy
3. `vault` — inicia em modo dev
4. `vault-init` — grava os secrets no Vault após Vault healthy
5. `app` — aguarda `postgres` healthy + `vault-init` completed

### 4. Subir apenas a infraestrutura (modo dev local)

```bash
# Sobe apenas PostgreSQL, Keycloak e Vault
docker compose up postgres postgres-keycloak keycloak vault vault-init

# Em outro terminal, rode a aplicação em modo dev
./gradlew quarkusDev
```

A aplicação estará disponível em: `http://localhost:8081`

### 5. Build manual

```bash
# Build padrão (fast-jar)
./gradlew build

# Build pulando testes
./gradlew build -x test

# Executar o jar gerado
java -jar build/quarkus-app/quarkus-run.jar
```

### 6. Build nativo (GraalVM)

```bash
# Com GraalVM instalado
./gradlew build -Dquarkus.native.enabled=true

# Sem GraalVM (via container)
./gradlew build -Dquarkus.native.enabled=true -Dquarkus.native.container-build=true
```

---

## Variáveis de Ambiente e Vault

### Como funciona

O Quarkus Vault Extension (`io.quarkiverse.vault:quarkus-vault:4.2.1`) injeta automaticamente os segredos do Vault como propriedades de configuração. Os valores definidos em `secret/olist-app` ficam disponíveis como `${db.username}`, `${db.password}`, etc., no `application.yml`.

### Secrets armazenados no Vault

| Chave | Descrição |
|---|---|
| `db.username` | Usuário do PostgreSQL |
| `db.password` | Senha do PostgreSQL |
| `db.host` | Host do PostgreSQL |
| `db.port` | Porta do PostgreSQL |
| `db.name` | Nome do banco de dados |
| `keycloak.client-secret` | Secret do client Keycloak |
| `jwt.secret` | Chave de assinatura JWT |

### Configuração do Vault no `application.yml`

```yaml
quarkus:
  vault:
    url: ${QUARKUS_VAULT_URL:http://localhost:8200}
    authentication:
      client-token: ${QUARKUS_VAULT_AUTHENTICATION_CLIENT_TOKEN:myroot}
    secret-config-kv-path: ${VAULT_SECRET_PATH:secret/olist-app}
```

### Acessar o Vault UI

```
URL:   http://localhost:8200
Token: myroot
```

### Verificar secrets via CLI

```bash
docker exec -it vault vault kv get -address=http://127.0.0.1:8200 secret/olist-app
```

### Variáveis de ambiente principais (`.env.example`)

| Variável | Descrição |
|---|---|
| `APP_PORT` | Porta da aplicação (padrão: `8081`) |
| `DB_HOST` / `DB_PORT` / `DB_NAME` | Conexão PostgreSQL |
| `DB_USERNAME` / `DB_PASSWORD` | Credenciais PostgreSQL |
| `KC_DB_*` | Banco dedicado do Keycloak |
| `KEYCLOAK_PORT` | Porta do Keycloak (padrão: `8080`) |
| `KEYCLOAK_REALM` | Realm (`spring-webflux`) |
| `KEYCLOAK_CLIENT_ID` | Client ID (`spring-webflux-client`) |
| `KEYCLOAK_CLIENT_SECRET` | Secret do client |
| `VAULT_PORT` | Porta do Vault (padrão: `8200`) |
| `VAULT_TOKEN` | Token root do Vault |
| `VAULT_SECRET_PATH` | Caminho dos secrets (`secret/olist-app`) |

---

## Segurança — Keycloak e JWT

### Realm configurado

O realm `spring-webflux` é importado automaticamente pelo Keycloak na inicialização via `realm-export.json`.

### Roles disponíveis

| Role | Permissões |
|---|---|
| `ROLE_USER` | Leitura (GET) em todos os recursos |
| `ROLE_MANAGER` | Leitura + Criação + Atualização |
| `ROLE_ADMIN` | Acesso total, incluindo DELETE |

### Usuários pré-configurados

| Usuário | Senha | Roles |
|---|---|---|
| `admin` | `admin123` | `ROLE_ADMIN`, `ROLE_USER` |
| `manager` | `manager123` | `ROLE_MANAGER`, `ROLE_USER` |
| `user` | `user123` | `ROLE_USER` |

### Fluxo de autenticação

```
1. POST /api/v1/auth/login  →  { username, password }
2. Keycloak valida credenciais e retorna access_token + refresh_token
3. Cliente usa Authorization: Bearer <access_token> nas requisições
4. Quarkus OIDC valida o token contra as chaves públicas do Keycloak
5. Roles extraídas do token e aplicadas via @RolesAllowed
```

### Obter token via curl

```bash
# Login
curl -X POST http://localhost:8081/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Usar o token
curl http://localhost:8081/api/v1/customers \
  -H "Authorization: Bearer <access_token>"
```

### Acessar o Keycloak Admin Console

```
URL:      http://localhost:8080
Usuário:  admin
Senha:    admin
```

---

## Endpoints da API

### Autenticação

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `POST` | `/api/v1/auth/login` | Login — retorna access + refresh token | Público |
| `POST` | `/api/v1/auth/refresh` | Renova o access token | Público |
| `POST` | `/api/v1/auth/logout` | Revoga o refresh token no Keycloak | Autenticado |

### Customers

| Método | Endpoint | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/v1/customers?page=0&size=20` | Lista paginada | USER |
| `GET` | `/api/v1/customers/{id}` | Busca por ID | USER |
| `GET` | `/api/v1/customers/state/{state}` | Filtra por estado | USER |
| `POST` | `/api/v1/customers` | Cria customer | MANAGER |
| `PUT` | `/api/v1/customers/{id}` | Atualiza customer | MANAGER |
| `DELETE` | `/api/v1/customers/{id}` | Remove customer | ADMIN |

### Products

| Método | Endpoint | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/v1/products?page=0&size=20` | Lista paginada | USER |
| `GET` | `/api/v1/products/{id}` | Busca por ID | USER |
| `GET` | `/api/v1/products/category/{category}` | Filtra por categoria | USER |
| `POST` | `/api/v1/products` | Cria produto | MANAGER |
| `PUT` | `/api/v1/products/{id}` | Atualiza produto | MANAGER |
| `DELETE` | `/api/v1/products/{id}` | Remove produto | ADMIN |

### Orders

| Método | Endpoint | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/v1/orders?page=0&size=20` | Lista paginada | USER |
| `GET` | `/api/v1/orders?customerId={id}` | Filtra por customer | USER |
| `GET` | `/api/v1/orders?status={status}` | Filtra por status | USER |
| `GET` | `/api/v1/orders/{id}` | Busca por ID | USER |
| `POST` | `/api/v1/orders` | Cria pedido | MANAGER |
| `PUT` | `/api/v1/orders/{id}` | Atualiza pedido | MANAGER |
| `DELETE` | `/api/v1/orders/{id}` | Remove pedido | ADMIN |

### Sellers

| Método | Endpoint | Descrição | Role |
|---|---|---|---|
| `GET` | `/api/v1/sellers?page=0&size=20` | Lista paginada | USER |
| `GET` | `/api/v1/sellers/{id}` | Busca por ID | USER |
| `POST` | `/api/v1/sellers` | Cria seller | MANAGER |
| `PUT` | `/api/v1/sellers/{id}` | Atualiza seller | MANAGER |
| `DELETE` | `/api/v1/sellers/{id}` | Remove seller | ADMIN |

### Health

| Método | Endpoint | Descrição | Auth |
|---|---|---|---|
| `GET` | `/api/v1/health` | Status da aplicação | Público |

---

## HATEOAS — Nível 3 de Maturidade

Cada resposta inclui um array `links` com as ações disponíveis para aquele recurso:

```json
{
  "customerId": "06b8999e2fba1a1fbc88172c00ba8bc7",
  "customerCity": "franca",
  "customerState": "SP",
  "links": [
    { "rel": "self",   "href": "/api/v1/customers/06b8999e...", "method": "GET" },
    { "rel": "update", "href": "/api/v1/customers/06b8999e...", "method": "PUT" },
    { "rel": "delete", "href": "/api/v1/customers/06b8999e...", "method": "DELETE" }
  ]
}
```

Respostas paginadas (`PageResponse<T>`) incluem links de navegação:

```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 99441,
  "totalPages": 4973,
  "links": [
    { "rel": "self", "href": "/api/v1/customers?page=0&size=20", "method": "GET" },
    { "rel": "next", "href": "/api/v1/customers?page=1&size=20", "method": "GET" },
    { "rel": "prev", "href": "/api/v1/customers?page=0&size=20", "method": "GET" }
  ]
}
```

---

## Rate Limiting

Implementado via `RateLimitFilter` (JAX-RS `ContainerRequestFilter`, `@Priority(Priorities.USER - 100)`):

- **Limite:** 100 requisições por minuto por IP
- **IP detectado via:** header `X-Forwarded-For`
- **Janela:** fixa de 60 segundos (reset ao expirar)
- **Headers de resposta:**

```
X-RateLimit-Limit: 100
X-RateLimit-Remaining: 87
```

- **Resposta ao exceder o limite (HTTP 429):**

```json
{
  "error": "Too Many Requests",
  "message": "Rate limit exceeded. Try again in 60 seconds."
}
```

Headers adicionais na resposta 429:
```
Retry-After: 60
X-RateLimit-Remaining: 0
```

---

## Refresh Token

O fluxo de Refresh Token é delegado inteiramente ao Keycloak via `KeycloakTokenClient` (REST Client reativo):

```
1. Login  →  access_token (5 min) + refresh_token (30 min)
2. access_token expirado  →  POST /api/v1/auth/refresh
3. Keycloak valida o refresh_token e emite novos tokens
4. Logout  →  POST /api/v1/auth/logout  →  revoga o refresh_token no Keycloak
```

### Configuração no `application.yml`

```yaml
quarkus:
  oidc:
    token:
      refresh-expired: true
      refresh-token-time-skew: 30S
```

### Exemplo de uso

```bash
# Refresh
curl -X POST http://localhost:8081/api/v1/auth/refresh \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'

# Logout
curl -X POST http://localhost:8081/api/v1/auth/logout \
  -H "Content-Type: application/json" \
  -d '{"refreshToken":"<refresh_token>"}'
```

---

## Liquibase — Versionamento do Banco

As migrations são executadas automaticamente na inicialização (`migrate-at-start: true`) via datasource JDBC. O Hibernate ORM e o Hibernate Reactive têm `schema-management.action: none` para não conflitar com o Liquibase.

### Changelogs

| Arquivo | Descrição |
|---|---|
| `001-create-tables.xml` | Criação de todas as tabelas e FKs |
| `002-create-indexes.xml` | Índices para performance |
| `003-import-data.xml` | Importação dos CSVs do dataset Olist |

### Tabelas criadas

| Tabela | Descrição |
|---|---|
| `customers` | 99.441 clientes |
| `sellers` | 3.095 vendedores |
| `products` | 32.951 produtos |
| `orders` | 99.441 pedidos |
| `order_items` | 112.650 itens de pedido |
| `order_payments` | 103.886 pagamentos |
| `order_reviews` | 99.224 avaliações |
| `geolocation` | Dados de geolocalização por CEP |
| `product_category_translation` | Tradução de categorias PT→EN |
| `refresh_tokens` | Controle de refresh tokens |

---

## Swagger UI

```
Swagger UI:   http://localhost:8081/q/swagger-ui
OpenAPI JSON: http://localhost:8081/q/openapi
```

### Autenticar no Swagger UI

1. Acesse `http://localhost:8081/q/swagger-ui`
2. Clique em **Authorize** (cadeado)
3. Faça login via `POST /api/v1/auth/login` para obter o token
4. Cole o `accessToken` no campo **BearerAuth**

---

## Testes

### Executar todos os testes

```bash
./gradlew test
```

### Estrutura dos testes

```
src/test/java/br/com/
├── interfaces/rest/
│   ├── AuthResourceTest.java       # Testes de integração — Auth
│   ├── CustomerResourceTest.java   # Testes de integração — Customers
│   └── ProductResourceTest.java    # Testes de integração — Products
├── application/service/
│   └── OrderServiceTest.java       # Testes unitários — OrderService
└── ExampleResourceTest.java
```

### Tecnologias de teste

| Lib | Uso |
|---|---|
| `quarkus-junit5` | Runner do Quarkus para testes |
| `quarkus-junit5-mockito` | Mocks com `@InjectMock` |
| `rest-assured` | Testes HTTP fluentes |
| `quarkus-test-security` | Simula autenticação com `@TestSecurity` |
| `quarkus-panache-mock` | Mock de repositórios Panache |

### Exemplo de teste com segurança simulada

```java
@Test
@TestSecurity(user = "admin", roles = {"ROLE_ADMIN"})
void testDelete() {
    Mockito.when(customerService.delete("cust-001"))
            .thenReturn(Uni.createFrom().voidItem());

    given()
        .when().delete("/api/v1/customers/cust-001")
        .then()
        .statusCode(204);
}
```

---

## Docker e Docker Compose

### Serviços

| Serviço | Imagem | Porta | Descrição |
|---|---|---|---|
| `postgres` | `postgres:14-alpine` | `5432` | Banco da aplicação |
| `postgres-keycloak` | `postgres:14-alpine` | `5433` | Banco do Keycloak |
| `keycloak` | `quay.io/keycloak/keycloak:24.0.1` | `8080` | Identity Provider |
| `vault` | `hashicorp/vault:1.15` | `8200` | Gerenciador de segredos |
| `vault-init` | `hashicorp/vault:1.15` | — | Inicializa secrets no Vault |
| `app` | `Dockerfile` (local) | `8081` | Aplicação Quarkus |

### Comandos úteis

```bash
# Subir tudo
docker compose up --build

# Subir em background
docker compose up -d --build

# Ver logs da aplicação
docker compose logs -f app

# Parar tudo
docker compose down

# Reset completo (remove volumes)
docker compose down -v

# Rebuild apenas da aplicação
docker compose up --build app
```

### Dockerfile — Multi-stage build

```
Stage 1 (builder): eclipse-temurin:21-jdk-alpine
  └── ./gradlew build -x test --no-daemon -Dquarkus.package.jar.type=fast-jar

Stage 2 (runtime): eclipse-temurin:21-jre-alpine
  ├── Usuário não-root (appuser:appgroup)
  ├── 4 camadas separadas (lib/, app/, quarkus/, *.jar)
  ├── CSVs do dataset copiados para ./data/
  ├── JVM tunada para containers (G1GC, MaxRAMPercentage=75%)
  └── HEALTHCHECK no /api/v1/health
```

---

## Boas Práticas Aplicadas

### Arquitetura
- **DDD** com separação clara de `domain`, `application`, `infrastructure` e `interfaces`
- **Dependency Inversion** — camadas superiores dependem de interfaces, não de implementações
- **Records** Java 21 para DTOs imutáveis
- **Programação reativa** end-to-end com Mutiny (`Uni<T>`)

### Segurança
- Tokens JWT validados pelo Keycloak (chaves públicas JWKS)
- Segredos nunca em código-fonte — gerenciados pelo Vault
- Usuário não-root no container Docker
- CORS configurado explicitamente
- Rate Limiting por IP para proteção contra abuso

### Banco de Dados
- Migrations versionadas com Liquibase (nunca `drop-and-create`)
- Hibernate ORM e Reactive com `schema-management.action: none` — Liquibase é a única fonte de verdade do schema
- Índices criados para colunas de busca frequente
- Repositórios com interface no domínio e implementação na infraestrutura

### API
- **HATEOAS Nível 3** — respostas com links de hipermídia
- Paginação em todos os endpoints de listagem
- Validação de entrada com Bean Validation (`@NotBlank`, `@Size`) via `quarkus-hibernate-validator`
- Códigos HTTP semânticos (201 Created, 204 No Content, 404, 422, 429)
- Exception Mapper global para respostas de erro padronizadas

### Observabilidade
- Health check endpoint (`/api/v1/health`) — público, sem autenticação
- Logs estruturados por pacote (`br.com` em DEBUG)
- Headers de Rate Limit em todas as respostas

### Build e Deploy
- Multi-stage Dockerfile (imagem final ~200MB)
- `depends_on` com `condition` no Compose para ordem de inicialização correta
- Variáveis de ambiente para override de configuração em produção
- `.env.example` documentado e seguro para commitar

---

## Licença

MIT License — veja o arquivo [LICENSE](LICENSE) para detalhes.
