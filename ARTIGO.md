# Artigo Técnico: Arquitetura Cloud-Native Reativa com Quarkus e Domain-Driven Design (DDD)

---

## Resumo

Com a evolução das arquiteturas de microsserviços e a consolidação de ambientes em nuvem baseados em containers e orquestradores (como Kubernetes), a demanda por aplicações com baixo consumo de memória, inicialização ultrarrápida (*fast startup time*) e alta densidade de execução tornou-se crítica. Este artigo apresenta uma análise aprofundada da concepção, arquitetura e implementação da **Olist E-Commerce API**, uma aplicação corporativa robusta desenvolvida com o framework **Quarkus** (versão 3.x), **Java 21**, e estruturada sob os preceitos do **Domain-Driven Design (DDD)** e da **Arquitetura Limpa (Clean Architecture)**. Abordam-se os fundamentos do modelo reativo orientado a eventos com *SmallRye Mutiny*, a segregação de responsabilidades em camadas, a gestão de identidade e controle de acesso com **Keycloak (OIDC/JWT)**, o gerenciamento seguro de segredos com **HashiCorp Vault**, a evolução esquemática com **Liquibase**, a maturidade de hipermídia (**HATEOAS Nível 3**) e o provisionamento multi-cloud com **Terraform** e **Kubernetes (Kustomize)**.

**Palavras-chave:** Quarkus, Domain-Driven Design, Java 21, Arquitetura Reativa, Mutiny, Keycloak, HashiCorp Vault, Kubernetes, Terraform.

---

## 1. Introdução

O ecossistema corporativo Java historicamente destacou-se pela robustez, ecossistema maduro e forte tipagem. No entanto, os frameworks tradicionais baseados em reflexão dinâmica em tempo de execução (*runtime reflection*), *class loading* dinâmico e compilação JIT tardia impõem desafios significativos em ambientes *cloud-native*, especialmente relacionados ao consumo excessivo de memória (*memory footprint*) e inicialização lenta (*cold starts*).

Nesse cenário, o **Quarkus** ("*Supersonic Subatomic Java*") surge como um divisor de águas, reimaginando o desenvolvimento Java para a nuvem. Ao mover a maior parte do processamento pesado de metadados, injeção de dependências e validação para o momento de compilação (*build time*), o Quarkus viabiliza execuções instantâneas e integração nativa com o **GraalVM**.

Aliado a esse ganho de desempenho, a complexidade intrínseca de domínios corporativos exige abordagens arquiteturais que preservem a integridade das regras de negócio e desacoplem as decisões de infraestrutura. O **Domain-Driven Design (DDD)**, articulado por Eric Evans, fornece as ferramentas táticas e estratégicas ideais para modelar o coração do software de forma pura e extensível.

Este artigo explora como o projeto **Olist E-Commerce API** une a excelência de engenharia de software do DDD com o poder computacional e reativo do Quarkus.

---

## 2. O Framework Quarkus: Pilares e Diferenciais

O Quarkus não é apenas uma biblioteca ou framework de injeção de dependências; é uma plataforma completa orientada a *Container First* e *Reactive Native*.

```
┌─────────────────────────────────────────────────────────────────┐
│                    Quarkus Execution Model                      │
│                                                                 │
│  ┌─────────────────────────┐       ┌─────────────────────────┐  │
│  │    Build-Time (AoT)     │       │     Runtime Execution   │  │
│  │ - Bytecode Analysis     │──────▶│ - Zero Reflection Cost  │  │
│  │ - Static Metamodel Gen  │       │ - Near-zero memory heap │  │
│  │ - Dead Code Elimination │       │ - Sub-millisecond boot  │  │
│  └─────────────────────────┘       └─────────────────────────┘  │
│                                                                 │
│  ┌───────────────────────────────────────────────────────────┐  │
│  │                 Dual Engine (Vert.x Core)                 │  │
│  │  [Non-blocking Reactive I/O] ◄─► [Virtual Threads / Worker] │  │
│  └───────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.1. Build-Time Optimization (Tailored for Cloud)
Diferente dos frameworks tradicionais (como Spring MVC tradicional e Jakarta EE clássico), o Quarkus adota o conceito de **Ahead-of-Time (AoT) Processing**:
- **Eliminação de Reflexão em Runtime:** Toda a resolução de anotações (JAX-RS, CDI, Hibernate) é realizada durante a compilação pelo motor **ArC** (implementação CDI sob medida para compilação estática).
- **Geração de Metamodelo Estático:** As dependências são conectadas em tempo de build, reduzindo drasticamente o *heap size* e o tempo de boot.
- **Preparação para GraalVM Native Image:** Como não há carregamento dinâmico de classes ou *proxies* pesados em tempo de execução, a geração de binários executáveis nativos (Linux ELF ou Windows EXE) é direta e altamente otimizada.

### 2.2. Paradigma Reativo com SmallRye Mutiny e Vert.x
No núcleo do Quarkus reside o **Eclipse Vert.x**, um *event-loop engine* não bloqueante de altíssima performance. No projeto Olist API, o padrão reativo é exposto através da biblioteca **SmallRye Mutiny**:
- **Uni:** Representa um fluxo assíncrono que emite 0 ou 1 item (ou falha), análogo ao conceito de `Mono` ou `Promise`.
- **Multi:** Representa um fluxo assíncrono com suporte a *backpressure* que emite 0 a $N$ itens (ou falha), análogo a `Flux` ou `Observable`.

Essa abordagem garante que threads do sistema operacional não fiquem bloqueadas aguardando I/O de rede ou consultas ao banco de dados PostgreSQL.

---

## 3. Arquitetura Orientada ao Domínio (DDD) e Separação de Camadas

A arquitetura do projeto implementa rigorosamente a **Onion Architecture / Hexagonal Architecture**, garantindo que o Domínio permaneça isolado e desacoplado de tecnologias de banco de dados, bibliotecas de terceiros ou protocolos de transporte.

```
       ┌────────────────────────────────────────────────────────┐
       │                   INTERFACES / REST                    │
       │     (JAX-RS Resources, HATEOAS, OpenAPI, Filters)      │
       │  ┌──────────────────────────────────────────────────┐  │
       │  │                   APPLICATION                    │  │
       │  │    (Use Cases, Services, DTOs, Mappers/Assemblers)│  │
       │  │  ┌────────────────────────────────────────────┐  │  │
       │  │  │                   DOMAIN                   │  │  │
       │  │  │  (Entities, Aggregates, Value Objects,     │  │  │
       │  │  │   Repository Interfaces, Domain Exceptions)│  │  │
       │  │  └────────────────────────────────────────────┘  │  │
       │  └──────────────────────────────────────────────────┘  │
       │                 INFRASTRUCTURE                         │
       │ (Panache Repositories, Keycloak, Vault, RateLimiting)  │
       └────────────────────────────────────────────────────────┘
```

### 3.1. Camada de Domínio (`br.com.domain`)
Representa o núcleo das regras de negócio. Contém:
- **Modelos/Entidades de Domínio:** `Customer`, `Order`, `OrderItem` (chave composta `OrderItemId`), `OrderPayment` (chave composta `OrderPaymentId`), `OrderReview`, `Product`, `Seller`, `Geolocation`, `RefreshToken`.
- **Contratos de Repositório (Interfaces):** Ex.: `CustomerRepository`, `OrderRepository`. A camada de domínio define *o que* precisa ser persistido ou buscado, sem conhecer *como* (SQL, NoSQL, etc.).
- **Exceções de Domínio:** `ResourceNotFoundException`, `BusinessException`, desacopladas de códigos HTTP.

### 3.2. Camada de Aplicação (`br.com.application`)
Orquestra os fluxos de trabalho e casos de uso da aplicação:
- **Services:** Implementam as regras de orquestração (ex.: `OrderService`, `CustomerService`), garantindo transações reativas (`@WithTransaction`).
- **DTOs (Data Transfer Objects):** Segregação estrita entre objetos de entrada (`*RequestDTO`) e objetos de saída (`*ResponseDTO`).
- **Mappers:** Conversores dedicados entre Entidades de Domínio e DTOs, evitando vazamento do modelo de banco de dados para a API externa.

### 3.3. Camada de Interfaces / REST (`br.com.interfaces.rest`)
Ponto de entrada da aplicação via HTTP/JSON:
- **Resources JAX-RS Reativos:** Expondo endpoints com retorno `Uni<T>` ou `Uni<Response>`.
- **HATEOAS Assemblers:** Criação de links hipermídia conforme o nível 3 do Modelo de Maturidade de Richardson.
- **Exception Mappers:** Captura global de exceções e padronização das respostas de erro (RFC 7807 - *Problem Details*).

### 3.4. Camada de Infraestrutura (`br.com.infrastructure`)
Implementação concreta dos contratos externos e detalhamento tecnológico:
- **Repositórios Panache Reativos:** Implementações das interfaces de domínio utilizando `Hibernate Reactive with Panache` e `Reactive PostgreSQL Client`.
- **Segurança & OIDC:** Configuração do Keycloak e validação de tokens JWT.
- **Vault Provider:** Integração com HashiCorp Vault para injeção de credenciais em tempo de execução.
- **Filtros HTTP:** Implementação do Rate Limiting via `ContainerRequestFilter`.

---

## 4. Estudo de Caso: Arquitetura da Olist E-Commerce API

O projeto modela o ecossistema de marketplace da **Olist**, permitindo operações sobre clientes, pedidos, pagamentos, avaliações, itens, produtos, vendedores e dados geoespaciais.

### 4.1. Fluxo de Execução Reativo Ponta a Ponta
A listagem abaixo ilustra a integração não bloqueante desde o Controller REST até a persistência no banco:

```java
// Interfaces / REST Resource
@Path("/api/v1/orders")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class OrderResource {

    @Inject
    OrderService orderService;

    @GET
    @Path("/{id}")
    @RolesAllowed({"ROLE_USER", "ROLE_ADMIN", "ROLE_MANAGER"})
    @Operation(summary = "Get order by ID")
    public Uni<OrderResponse> findById(@PathParam("id") String id) {
        return service.findById(id);
    }
}
```

No nível de serviço, a execução assíncrona utiliza operadores reativos do Mutiny:

```java
// Application Service
@ApplicationScoped
public class OrderService {

    @Inject
    OrderRepository repository;

    @Inject
    OrderMapper mapper;

    @WithSession
    public Uni<OrderResponse> findById(String id) {
        return repository.findById(id)
            .onItem().ifNull().failWith(() -> new ResourceNotFoundException("Order not found: " + id))
            .map(mapper::toResponse);
    }
}
```

### 4.2. Maturidade HATEOAS (Richardson Maturity Model Nível 3)
A API fornece navegabilidade dinâmica através de hipermídia. Cada recurso retornado inclui a coleção `_links` apontando para si mesmo e ações relacionadas (ex.: pagamentos do pedido, itens, cliente associado):

```json
{
  "orderId": "e481f51cbdc54678b7cc49136f2d6af7",
  "customerId": "9ef432eb6251297304e76186b10a928d",
  "orderStatus": "delivered",
  "orderPurchaseTimestamp": "2017-10-02T10:56:33",
  "links": [
    { "rel": "self",     "href": "/api/v1/orders/e481f51cbdc54678b7cc49136f2d6af7",          "method": "GET" },
    { "rel": "customer", "href": "/api/v1/customers/9ef432eb6251297304e76186b10a928d",       "method": "GET" },
    { "rel": "items",    "href": "/api/v1/orders/e481f51cbdc54678b7cc49136f2d6af7/items",    "method": "GET" },
    { "rel": "payments", "href": "/api/v1/orders/e481f51cbdc54678b7cc49136f2d6af7/payments", "method": "GET" },
    { "rel": "update",   "href": "/api/v1/orders/e481f51cbdc54678b7cc49136f2d6af7",          "method": "PUT" },
    { "rel": "delete",   "href": "/api/v1/orders/e481f51cbdc54678b7cc49136f2d6af7",          "method": "DELETE" }
  ]
}
```

### 4.3. Versionamento de Banco de Dados com Liquibase
Para garantir reprodutibilidade e integridade em ambientes de CI/CD e produção, as alterações de esquema e carga inicial de dados são gerenciadas pelo **Liquibase**. O pipeline executa migrações estruturadas via changelogs XML/SQL, garantindo rastreabilidade através da tabela de controle `databasechangelog`.

### 4.4. Cobertura Completa de Endpoints REST

A API exposta cobre todos os recursos do dataset Olist com controle de acesso granular por role:

| Recurso | Métodos | Roles |
|---|---|---|
| `/api/v1/auth` | POST login, refresh, logout | Público / Autenticado |
| `/api/v1/customers` | GET (lista, por ID, por estado), POST, PUT, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/products` | GET (lista, por ID, por categoria), POST, PUT, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/orders` | GET (lista, por ID, por customer, por status), POST, PUT, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/orders/{id}/items` | GET, POST, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/orders/{id}/payments` | GET, POST, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/orders/{id}/reviews` | GET | USER |
| `/api/v1/reviews` | GET por ID, POST, PUT, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/sellers` | GET (lista, por ID), POST, PUT, DELETE | USER / MANAGER / ADMIN |
| `/api/v1/geolocation` | GET por CEP, GET por estado (paginado) | USER |
| `/api/v1/users` | GET, POST, PUT, DELETE (Keycloak Admin API) | ADMIN |
| `/api/v1/health` | GET | Público |

### 4.5. Dataset Olist e Volumes de Dados

Os dados utilizados são do dataset público **Olist Brazilian E-Commerce** (Kaggle), importados automaticamente via Liquibase na inicialização da aplicação. Isso permite testar a API com dados reais de produção desde o primeiro boot:

| Tabela | Registros |
|---|---|
| `customers` | 99.441 |
| `sellers` | 3.095 |
| `products` | 32.951 |
| `orders` | 99.441 |
| `order_items` | 112.650 |
| `order_payments` | 103.886 |
| `order_reviews` | 99.224 |
| `geolocation` | 1.000.163 |

---

## 5. Segurança, Governança de Segredos e Resiliência

```
                        ┌─────────────────────────────────────┐
                        │             Requisição              │
                        └──────────────────┬──────────────────┘
                                           │
                                  ┌────────▼────────┐
                                  │  Rate Limiting  │ (Sliding Window / Leaky Bucket)
                                  └────────┬────────┘
                                           │
                                  ┌────────▼────────┐
                                  │ Keycloak (OIDC) │ (Validação de Assinatura JWT & RBAC)
                                  └────────┬────────┘
                                           │
                                  ┌────────▼────────┐
                                  │ HashiCorp Vault │ (Segredos em Repouso & Rotação)
                                  └────────┬────────┘
                                           │
                                  ┌────────▼────────┐
                                  │ Fault Tolerance │ (Circuit Breaker, Retry, Fallback)
                                  └─────────────────┘
```

### 5.1. Autenticação e Autorização com Keycloak (OIDC & JWT)
A segurança da API adota o protocolo **OpenID Connect (OIDC)** com tokens **JSON Web Token (JWT)** assimétricos (RS256). O Quarkus (`quarkus-oidc` e `quarkus-smallrye-jwt`) atua como um *Resource Server*, validando as assinaturas dos tokens através do *JWKS endpoint* do Keycloak. O controle de acesso é baseado em perfis (**RBAC - Role-Based Access Control**):
- `ROLE_ADMIN`: Acesso total a operações de escrita, exclusão, auditoria e gerenciamento de usuários via Keycloak Admin API.
- `ROLE_MANAGER`: Acesso a operações de criação e atualização de inventário, pedidos e produtos.
- `ROLE_USER`: Acesso de leitura em todos os recursos.

### 5.2. Gestão Centralizada de Segredos com HashiCorp Vault
A API elimina completamente credenciais *hardcoded* ou expostas em arquivos de configuração locais. Através da extensão `quarkus-vault`, segredos como credenciais do PostgreSQL, chaves de criptografia e credenciais administrativas são injetados em tempo de inicialização diretamente do mecanismo de armazenamento seguro do Vault.

### 5.3. Resiliência e Proteção contra Abuso (Rate Limiting & Fault Tolerance)
- **Rate Limiting:** Implementado na camada de filtro HTTP para conter ataques de força bruta e negação de serviço (DoS), limitando requisições por IP de origem (ex.: 100 req/min).
- **SmallRye Fault Tolerance:** Anotações como `@Timeout`, `@Retry` e `@CircuitBreaker` são aplicadas nas integrações externas, garantindo degradação graciosa em cenários de instabilidade de rede.

---

## 6. Infraestrutura Cloud-Native, Orquestração e Multi-Cloud

A entrega contínua e escalabilidade do projeto são sustentadas por uma stack moderna de infraestrutura como código (IaC) e orquestração de containers.

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       ESTRATÉGIA MULTI-CLOUD (IaC)                          │
├──────────────────────────┬──────────────────────────┬───────────────────────┤
│       Amazon Web         │      Microsoft Azure     │     Google Cloud      │
│     Services (AWS)       │                          │     Platform (GCP)    │
├──────────────────────────┼──────────────────────────┼───────────────────────┤
│ • EKS (Elastic K8s)      │ • AKS (Azure K8s)        │ • GKE (Google K8s)    │
│ • ECR (Registry)         │ • ACR (Container Reg)    │ • Artifact Registry   │
│ • RDS PostgreSQL         │ • Azure Database for PG  │ • Cloud SQL PG        │
└──────────────────────────┴──────────────────────────┴───────────────────────┘
                                   │
                                   ▼
             ┌───────────────────────────────────────────┐
             │         KUBERNETES + KUSTOMIZE            │
             │   - Base: Deployments, Services, Ingress  │
             │   - Overlays: AWS, Azure, GCP             │
             └───────────────────────────────────────────┘
```

### 6.1. Orquestração com Kubernetes e Kustomize
A configuração do Kubernetes é organizada modularmente utilizando **Kustomize**:
- **Base:** Define o estado desejado comum a todos os ambientes (ReplicaSets, Probes de Liveness/Readiness, ConfigMaps, Services e Ingress).
- **Overlays (`aws`, `azure`, `gcp`):** Aplicam *patches* específicos de provedor (ex.: anotações de Load Balancer gerenciado, StorageClasses específicas e recursos de memória/CPU).

### 6.2. Infraestrutura como Código (Terraform)
Os recursos de nuvem (clusters Kubernetes, bancos gerenciados e registros de imagens Docker) são provisionados declarativamente através de módulos Terraform reutilizáveis:
- Módulo `database`: Provisiona instâncias gerenciadas do PostgreSQL em alta disponibilidade.
- Módulo `k8s-cluster`: Provisiona os nós de computação e planos de controle (EKS/AKS/GKE).
- Módulo `registry`: Provisiona repositórios seguros de imagens de containers.

---

## 7. Tabela Comparativa: Abordagem Tradicional vs. Quarkus DDD

| Dimensão de Análise | Arquitetura Tradicional (Spring Boot / MVC Bloqueante) | Arquitetura Adotada (Quarkus + DDD Reativo) |
|---|---|---|
| **Tempo de Inicialização** | 5 a 15 segundos (JIT & reflexão pesada) | 0.05 a 1.2 segundos (Build-time optimization) |
| **Consumo de Memória (RSS)** | 250 MB – 600 MB por instância | 30 MB – 90 MB por instância |
| **Modelo de Execução I/O** | Thread-per-request (Bloqueante) | Event-Loop Reativo Não Bloqueante (Mutiny / Vert.x) |
| **Acoplamento de Domínio** | Frequente vazamento de anotações ORM nas regras | Domínio puro e isolado via Inversão de Dependências |
| **Segurança & Segredos** | Credenciais em variáveis de ambiente estáticas | Injeção dinâmica via HashiCorp Vault + Keycloak OIDC |
| **Prontidão para Cloud/K8s** | Alto custo para auto-scaling rápido | Ideal para Serverless, Knative e Auto-scaling em K8s |

---

## 8. Conclusão

A arquitetura desenvolvida para a **Olist E-Commerce API** demonstra com clareza a viabilidade e a superioridade técnica de se construir sistemas empresariais modernos em Java utilizando o **Quarkus** aliado ao **Domain-Driven Design**.

A dissociação entre as regras de negócio puras (no Domínio) e as preocupações de infraestrutura e persistência permitiu criar um software sustentável, extensível e de alta manutenibilidade. Ao mesmo tempo, a adoção do motor reativo do Quarkus resultou em ganhos expressivos de densidade computacional, eficiência energética e redução de custos operacionais em nuvem (*Total Cost of Ownership - TCO*).

A inclusão de padrões de excelência operacional — como segurança corporativa via Keycloak, cofre de senhas centralizado com Vault, hipermídia HATEOAS, tolerância a falhas e infraestrutura declarativa multi-cloud — consolida o projeto como uma referência prática de arquitetura de software para a era *Cloud-Native*.

---

## 9. Referências Bibliográficas

1. **EVANS, Eric.** *Domain-Driven Design: Tackling Complexity in the Heart of Software*. Addison-Wesley Professional, 2003.
2. **VERNON, Vaughn.** *Implementing Domain-Driven Design*. Addison-Wesley Signature Series, 2013.
3. **MARTIN, Robert C.** *Clean Architecture: A Craftsman's Guide to Software Structure and Design*. Prentice Hall, 2017.
4. **QUARKUS COMMUNITY.** *Quarkus: Supersonic Subatomic Java Documentation*. Disponível em: <https://quarkus.io/guides/>. Acesso em: 2025.
5. **RED HAT.** *SmallRye Mutiny: Intuitive Event-Driven Reactive Programming Library for Java*. Disponível em: <https://smallrye.io/smallrye-mutiny/>. Acesso em: 2025.
6. **RICHARDSON, Leonard.** *Maturity Model: Steps toward the glory of REST*. MartinFowler.com, 2010.
7. **HASHICORP.** *HashiCorp Vault Documentation & Best Practices*. HashiCorp Inc., 2024.
8. **KEYCLOAK COMMUNITY.** *Keycloak Server Administration and OpenID Connect Guide*. Red Hat, 2024.
9. **KUBERNETES AUTHORS.** *Declarative Management of Kubernetes Objects Using Kustomize*. Kubernetes.io Documentation, 2025.
10. **OLIST & KAGGLE.** *Brazilian E-Commerce Public Dataset by Olist*. Kaggle Datasets, 2018.
