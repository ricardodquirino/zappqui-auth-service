# Zappqui Auth Service

Serviço de autenticação e autorização para a plataforma Zappqui, desenvolvido com Spring Boot 3.4.0 e Java 21. Fornece endpoints para autenticação de usuários, emissão de tokens JWT e validação de tokens.

## 📋 Sobre o Projeto

O **Zappqui Auth Service** é um microserviço responsável por:
- Autenticação de usuários
- Geração de tokens JWT
- Validação de tokens
- Gerenciamento de usuários
- Cache de dados com Caffeine

## 🛠️ Tecnologias Utilizadas

- **Linguagem**: Java 21
- **Framework**: Spring Boot 3.4.0
- **Gerenciador de Dependências**: Gradle
- **Banco de Dados**: PostgreSQL 17
- **Cache**: Redis 7 + Spring Cache com Caffeine
- **Autenticação**: Spring Security + JWT (JJWT 0.12.6)
- **Migração de BD**: Flyway
- **Logging**: Log4j2
- **Documentação da API**: Swagger OpenAPI 2.7.0
- **Monitoramento**: Spring Boot Actuator
- **Containerização**: Docker & Docker Compose

## 📦 Dependências Principais

```gradle
- org.springframework.boot:spring-boot-starter-web
- org.springframework.boot:spring-boot-starter-security
- org.springframework.boot:spring-boot-starter-data-jpa
- org.springframework.boot:spring-boot-starter-cache
- org.springframework.boot:spring-boot-starter-actuator
- io.jsonwebtoken:jjwt-api:0.12.6
- com.github.ben-manes.caffeine:caffeine:3.1.8
- org.postgresql:postgresql:42.7.3
- org.flywaydb:flyway-core
- org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0
```

## 🚀 Como Baixar e Configurar

### Pré-requisitos

- **Java 21** ou superior
- **Gradle** (opcional, pode usar o gradlew incluído)
- **Docker & Docker Compose** (para execução containerizada)
- **PostgreSQL 17** (se rodar localmente sem Docker)
- **Redis 7** (para cache distribuído)

### 1. Clonar o Repositório

```bash
git clone https://github.com/seu-usuario/zappqui-auth-service.git
cd zappqui-auth-service
```

### 2. Configurar Variáveis de Ambiente (Opcional)

Se rodar localmente, certifique-se de que PostgreSQL e Redis estejam rodando com as seguintes credenciais padrão (em `application.yaml`):

- **Banco de Dados**: 
  - Host: `localhost:5433`
  - Database: `zappqui_auth`
  - User: `zappqui`
  - Password: `zappqui123`

- **Redis**:
  - Host: `localhost`
  - Port: `6379`

## 🏃 Como Rodar o Projeto

### Opção 1: Execução Local (sem Docker)

#### 1.1 Iniciar PostgreSQL e Redis

```bash
# PostgreSQL
docker run -d \
  --name zappqui-auth-db \
  -e POSTGRES_DB=zappqui_auth \
  -e POSTGRES_USER=zappqui \
  -e POSTGRES_PASSWORD=zappqui123 \
  -p 5433:5432 \
  postgres:17-alpine

# Redis
docker run -d \
  --name zappqui-auth-redis \
  -p 6379:6379 \
  redis:7-alpine
```

#### 1.2 Compilar e Executar a Aplicação

```bash
# Usando Gradle Wrapper (sem precisar instalar Gradle)
./gradlew clean build

# Executar a aplicação
./gradlew bootRun
```

Ou:

```bash
# Build JAR
./gradlew clean build

# Executar JAR
java -jar build/libs/zappqui-auth-service-1.0.0-SNAPSHOT.jar
```

A aplicação estará disponível em: `http://localhost:8082`

### Opção 2: Execução com Docker Compose (Recomendado)

#### 2.1 Iniciar todos os serviços

```bash
docker-compose up -d
```

Este comando inicia:
- **PostgreSQL 17** em `localhost:5433`
- **Redis 7** em `localhost:6379`
- **Aplicação Spring Boot** em `localhost:8082`

#### 2.2 Parar os serviços

```bash
docker-compose down
```

#### 2.3 Ver logs

```bash
docker-compose logs -f app
```

#### 2.4 Recriar containers

```bash
docker-compose up -d --build
```

#### 2.5 Parar e remover volumes (limpeza completa)

```bash
docker-compose down -v
```

### Opção 3: Build e execução da imagem Docker manualmente

Se você quiser construir e rodar apenas o container da aplicação (sem Docker Compose), siga os passos abaixo.

#### 3.1 Build da imagem Docker

```bash
docker build -t zappqui-auth-service .
```

> O `Dockerfile` usa **multi-stage build**:
> 1. **Estágio de build** (`eclipse-temurin:21-jdk-alpine`): compila o projeto com Gradle e gera o JAR
> 2. **Estágio de runtime** (`eclipse-temurin:21-jre-alpine`): imagem leve apenas com o JRE para rodar o JAR

#### 3.2 Rodar o container

```bash
docker run -d \
  --name zappqui-auth-app \
  -p 8082:8082 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5433/zappqui_auth \
  -e SPRING_DATASOURCE_USERNAME=zappqui \
  -e SPRING_DATASOURCE_PASSWORD=zappqui123 \
  -e SPRING_REDIS_HOST=host.docker.internal \
  -e SPRING_REDIS_PORT=6379 \
  zappqui-auth-service
```

> ⚠️ Para esse comando funcionar, o PostgreSQL e o Redis precisam estar rodando no host (ou em outros containers acessíveis). Use `host.docker.internal` para acessar serviços do host a partir do container.

#### 3.3 Verificar se o container está rodando

```bash
docker ps
```

Saída esperada:
```
CONTAINER ID   IMAGE                    STATUS                    PORTS                    NAMES
abc123def456   zappqui-auth-service     Up 30s (healthy)          0.0.0.0:8082->8082/tcp   zappqui-auth-app
```

#### 3.4 Ver logs do container

```bash
docker logs -f zappqui-auth-app
```

#### 3.5 Parar e remover o container

```bash
docker stop zappqui-auth-app
docker rm zappqui-auth-app
```

#### 3.6 Remover a imagem

```bash
docker rmi zappqui-auth-service
```

## 🐳 Estrutura do Dockerfile

O projeto utiliza um **Dockerfile multi-stage** para otimizar o tamanho da imagem final:

```
┌──────────────────────────────────────────────┐
│  Estágio 1: BUILD (eclipse-temurin:21-jdk)   │
│                                              │
│  1. Copia arquivos do Gradle                 │
│  2. Baixa dependências (camada cacheada)     │
│  3. Copia código-fonte                       │
│  4. Compila e gera o JAR                     │
└──────────────────┬───────────────────────────┘
                   │
                   ▼
┌──────────────────────────────────────────────┐
│  Estágio 2: RUNTIME (eclipse-temurin:21-jre) │
│                                              │
│  1. Copia apenas o JAR do estágio anterior   │
│  2. Roda com usuário não-root (segurança)    │
│  3. Health check via /actuator/health        │
│  4. Expõe porta 8082                         │
└──────────────────────────────────────────────┘
```

**Benefícios**:
- 🏗️ Imagem final ~200MB (apenas JRE, sem JDK/Gradle/código-fonte)
- 🔒 Roda com usuário não-root
- ❤️ Health check integrado
- ⚡ Cache de dependências do Gradle (builds mais rápidos)

## 🐳 Arquitetura Docker Compose

```
┌─────────────────────────────────────────────────────────┐
│                   zappqui-network                       │
│                                                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐  │
│  │  app          │  │ postgres-auth │  │  redis-auth  │  │
│  │  :8082→8082   │  │  :5433→5432   │  │  :6379→6379  │  │
│  │  Spring Boot  │──│  PostgreSQL   │  │  Redis 7     │  │
│  │              │  │  17-alpine    │  │  alpine      │  │
│  └──────────────┘  └──────────────┘  └──────────────┘  │
│        │                   ▲                ▲           │
│        └───────────────────┴────────────────┘           │
│              depends_on (healthy)                       │
└─────────────────────────────────────────────────────────┘
```

| Serviço | Container | Imagem | Porta |
|---|---|---|---|
| **app** | `zappqui-auth-app` | Build local (`Dockerfile`) | `8082:8082` |
| **postgres-auth** | `zappqui-auth-db` | `postgres:17-alpine` | `5433:5432` |
| **redis-auth** | `zappqui-auth-redis` | `redis:7-alpine` | `6379:6379` |

## 📊 Banco de Dados

### Configuração PostgreSQL

- **Versão**: PostgreSQL 17 Alpine
- **Nome do banco**: `zappqui_auth`
- **Usuário**: `zappqui`
- **Senha**: `zappqui123`
- **Porta**: `5433` (mapeada para 5432 do container)

### Migrações (Flyway)

As migrações SQL estão localizadas em: `src/main/resources/db/migration/`

Exemplo:
- `V1__create_table_user.sql` - Criação da tabela de usuários

O Flyway executa automaticamente na inicialização da aplicação.

### Configuração Hibernate

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: none  # Flyway gerencia o schema
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
```

## 💾 Cache

### Redis + Caffeine (L2 Cache)

A aplicação utiliza dois níveis de cache:

1. **Caffeine** (L1 - In-Memory): Cache local rápido
2. **Redis** (L2 - Distribuído): Cache compartilhado entre instâncias

```yaml
spring:
  cache:
    type: redis
  redis:
    host: localhost
    port: 6379
```

### Configuração do Gradle para Cache

```groovy
implementation 'org.springframework.boot:spring-boot-starter-cache'
implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'
```

## 🔗 Endpoints da API

### 1. Health Check (Spring Boot Actuator)

**GET** `/actuator/health`

Verifica o status da aplicação e suas dependências.

**Response (200 OK)**:
```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP"
    },
    "redis": {
      "status": "UP"
    }
  }
}
```

### 2. Criar Usuário

**POST** `/users`

Cria um novo usuário no sistema.

> ⚠️ **Regras de senha**: mínimo 8 caracteres, incluindo pelo menos 1 letra maiúscula, 1 minúscula, 1 número e 1 caractere especial (`@$!%*?&`).

**Request Body**:
```json
{
  "username": "joao.silva",
  "password": "Senha@123"
}
```

**Response (200 OK)**:
```json
{
  "id": 1,
  "username": "joao.silva"
}
```

**Status Codes**:
- `200 OK` - Usuário criado com sucesso
- `400 Bad Request` - Dados inválidos (username duplicado, senha fraca, campos obrigatórios ausentes)

### 3. Login (Autenticação)

**POST** `/auth/login`

Autentica um usuário e retorna um token JWT.

**Request Body**:
```json
{
  "username": "joao.silva",
  "password": "Senha@123",
  "ttlSeconds": 3600
}
```

> O campo `ttlSeconds` é opcional. Se for `0` ou não informado, o token terá validade padrão de 900 segundos (15 minutos).

**Response (200 OK)**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Status Codes**:
- `200 OK` - Login bem-sucedido
- `401 Unauthorized` - Credenciais inválidas

### 4. Validar Token

**POST** `/auth/validate`

Valida um token JWT e retorna o subject (username).

**Request Body**:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9..."
}
```

**Response (200 OK) — Token válido**:
```json
{
  "valid": true,
  "subject": "joao.silva"
}
```

**Response (200 OK) — Token inválido/expirado**:
```json
{
  "valid": false,
  "subject": null
}
```

## 📚 Documentação da API (Swagger / OpenAPI)

A API possui documentação interativa completa via **Swagger UI**, gerada automaticamente pelo **SpringDoc OpenAPI 2.7.0**.

### Como acessar

Com a aplicação rodando, acesse:

| Recurso | URL |
|---|---|
| **Swagger UI** | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) |
| **OpenAPI JSON** | [http://localhost:8082/v3/api-docs](http://localhost:8082/v3/api-docs) |
| **OpenAPI YAML** | [http://localhost:8082/v3/api-docs.yaml](http://localhost:8082/v3/api-docs.yaml) |

### O que está documentado

O Swagger inclui documentação completa de todos os endpoints da API:

- **Autenticação** (`/auth`)
  - `POST /auth/login` — Login e geração de token JWT
  - `POST /auth/validate` — Validação de token JWT
- **Usuários** (`/users`)
  - `POST /users` — Criação de novo usuário

Cada endpoint inclui:
- ✅ Descrição detalhada da operação
- ✅ Exemplos de request/response
- ✅ Schemas dos DTOs com exemplos de valores
- ✅ Códigos de status HTTP documentados
- ✅ Validações de campos obrigatórios

### Testando via Swagger UI

1. Acesse `http://localhost:8082/swagger-ui.html`
2. Expanda o endpoint desejado
3. Clique em **"Try it out"**
4. Preencha os campos do request body
5. Clique em **"Execute"**
6. Veja a resposta diretamente na interface

## 🔐 Segurança

- **Spring Security** para autenticação e autorização
- **JWT (JJWT 0.12.6)** para tokens seguros
- **Senha**: Codificada com BCrypt (padrão do Spring Security)

## 📝 Logs

A aplicação utiliza **Log4j2** para logging estruturado.

Arquivo de configuração: (criar se necessário) `src/main/resources/log4j2.xml`

## 🧪 Testes

Executar testes unitários:

```bash
./gradlew test
```

A aplicação inclui:
- Spring Boot Test
- Spring Security Test
- H2 Database (para testes em memória)

## 📋 Variáveis de Ambiente (Docker Compose)

```yaml
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres-auth:5432/zappqui_auth
SPRING_DATASOURCE_USERNAME=zappqui
SPRING_DATASOURCE_PASSWORD=zappqui123
SPRING_REDIS_HOST=redis-auth
SPRING_REDIS_PORT=6379
```

## 🐛 Troubleshooting

### Erro: Conexão recusada no PostgreSQL

```
org.postgresql.util.PSQLException: Connection to localhost:5433 refused
```

**Solução**: Certifique-se de que o PostgreSQL está rodando:

```bash
docker ps | grep zappqui-auth-db
```

### Erro: Redis não disponível

```
redis.clients.jedis.exceptions.JedisConnectionException
```

**Solução**: Reinicie o Redis:

```bash
docker-compose restart redis-auth
```

### Limpeza completa

```bash
docker-compose down -v
docker-compose up -d
```

## 📞 Suporte

Para dúvidas ou issues, abra uma issue no repositório ou entre em contato com a equipe de desenvolvimento.

## 📄 Licença

Este projeto está licenciado sob a MIT License. Veja o arquivo LICENSE para mais detalhes.

---

**Desenvolvido com ❤️ para Zappqui**
